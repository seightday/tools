package com.seightday.builder

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Slf4j
@Component
class BackupService implements CommandLineRunner{

	@Value('${db.username}')
	String dbUser

	@Value('${db.password}')
	String dbPwd

	@Value('${db.name}')
	String dbName

	@Value('${db.host}')
	String dbHost
	

	@Value('${part.data.tables}')
	String[] partDataTables
	@Value('${ignore.tables}')
	String ignoreTables

	@Value('${dump.exe}')
	String dumpExe
	@Value('${dump.params}')
	String dumpParams
	@Value('${ignore.views}')
	boolean igoreViews
	@Value('${mysql.exe}')
	String mysqlExe
	@Value('${output.dir}')
	String outputDir
	@Value('${dump.execute}')
	boolean dumpExecute

	@Value('${db.source.username}')
	String dbSourceUser

	@Value('${db.source.password}')
	String dbSourcePwd

	@Value('${db.source.name}')
	String dbSourceName

	@Value('${db.source.host}')
	String dbSourceHost

	@Override
	void run(String... arg0) throws Exception {
		outputDir+='/'+LocalDate.now().format(DateTimeFormatter.ofPattern('yyyyMMdd'))

		def echoBatDateTime = 'echo %date:~0,10% %time:~0,8%'
		def dumpFinished='mshta vbscript:msgbox("dump finish",36,"dump finish")(window.close)'
		def sourceFinished='mshta vbscript:msgbox("source finish",36,"source finish")(window.close)'

		List<String> dumpBatCmd=[]
		dumpBatCmd.add(echoBatDateTime)

		List<String> viewList=[]
		if (igoreViews){
			def command="${mysqlExe} -u${dbUser} -p${dbPwd} -h ${dbHost} INFORMATION_SCHEMA --skip-column-names -e  \"select table_name from tables where table_type = 'VIEW' and table_schema = '${dbName}'\""
			def process = command.execute()
			viewList = printProcess(process)
			log.info('viewList is {}',viewList)
		}

		//mysqldump --help
		StringBuilder sb=new StringBuilder();
		if (ignoreTables){//TODO 生成忽略的表的结构 --no-data
			ignoreTables.split(',').each {
				sb.append(" --ignore-table=${dbName}.$it ")
			}
		}

		if(viewList){
			viewList.each {
				sb.append(" --ignore-table=${dbName}.$it ")
			}
		}

		partDataTables.each {
			def split=it.split('##')
			sb.append(" --ignore-table=${dbName}.${split[0]} ")
		}

		def all="$dumpExe $dumpParams -u$dbUser -p$dbPwd -h $dbHost $dbName $sb > $outputDir/all.sql"
		dumpBatCmd.add(all.toString())
		dumpBatCmd.add(echoBatDateTime)

		partDataTables.each {
			def split=it.split('##')
			def cmd="$dumpExe $dumpParams -u$dbUser -p$dbPwd -h $dbHost $dbName ${split[0]} --where=\"${split[1]}\" > $outputDir/${split[0]}.sql"
			dumpBatCmd.add(cmd.toString())
			dumpBatCmd.add(echoBatDateTime)
		}

		dumpBatCmd.add(dumpFinished)
		dumpBatCmd.add('pause')

		dumpBatCmd.each {
			println(it)
		}

		def dumpBat = "${outputDir}/dump.bat"
		FileUtils.writeLines(new File(dumpBat),dumpBatCmd)
		if (dumpExecute){
			"cmd /c start ${dumpBat}".execute()
		}


		List<String> sourceBatCmd=[]
		sourceBatCmd.add(echoBatDateTime)
		//mysql --verbose 可在bat中输出mysql的命令日志，但是出现造成mysql进程无反应的情况
		sourceBatCmd.add("${mysqlExe} -u${dbSourceUser} -p${dbSourcePwd} -h ${dbSourceHost} ${dbSourceName} -e \"source ${outputDir}/all.sql\"")
		sourceBatCmd.add(echoBatDateTime)

		partDataTables.each {
			sourceBatCmd.add("${mysqlExe} -u${dbSourceUser} -p${dbSourcePwd} -h ${dbSourceHost} ${dbSourceName} -e \"source $outputDir/${it.split('##')[0]}.sql\"")
			sourceBatCmd.add(echoBatDateTime)
		}
		sourceBatCmd.add(sourceFinished)
		sourceBatCmd.add('pause')
		sourceBatCmd.each {
			println(it)
		}
		FileUtils.writeLines(new File("${outputDir}/source.bat"),sourceBatCmd)

	}


	List<String> printProcess(Process p){
		log.info("printProcess")
		List<String> resultList=new ArrayList<>()
		def line=null

		def reader = new BufferedReader(new InputStreamReader(p.getInputStream(), 'gbk'))
		while((line=reader.readLine())!=null){
			resultList.add(line)
			log.info(line)
		}
		resultList
	}

	
	

	
}
