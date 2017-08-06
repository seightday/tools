package com.seightday.builder

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Slf4j
@Component
class BackupService implements CommandLineRunner{
	
	@Value('${db.password}')
	String dbPwd
	
	@Value('${db.username}')
	String dbUser

	@Value('${part.data.tables}')
	String[] partDataTables
	@Value('${ignore.tables}')
	String ignoreTables
	@Value('${db.name}')
	String dbName
	@Value('${db.host}')
	String dbHost
	@Value('${sql.dir}')
	String sqlDir
	@Value('${sql.source.dir}')
	String sqlSourceDir
	@Value('${dump.exe}')
	String dumpExe
	@Value('${dump.params}')
	String dumpParams

	@Override
	void run(String... arg0) throws Exception {
		//mysqldump不要锁表，不生成注释
		//mysqldump --help
		StringBuilder sb=new StringBuilder();
		if (ignoreTables){
			ignoreTables.split(',').each {
				sb.append(" --ignore-table=${dbName}.$it ")
			}
		}
		partDataTables.each {
			def split=it.split('##')
			sb.append(" --ignore-table=${dbName}.${split[0]} ")
		}

		def dump="$dumpExe $dumpParams -u$dbUser -p$dbPwd -h $dbHost $dbName $sb > $sqlDir/all.sql"
		println(dump)
		//mysqldump -t -uroot -pwtsd123 -h 192.168.0.185 sms --tables tb_sale_detail > C:\Users\Administrator\Desktop\saledetail.sql//只导指定表的数据，会锁表
//		dump.execute()
//		printProcess(dump.execute())
		//mysqldump -uoms -poms -h 192.168.0.198 oms --ignore-table=oms.tb_clear_paydata_detail --ignore-table=oms.tb_biz_refund_record > C:\Users\Administrator\Desktop\data\ignore.sql
//		print(ignoreTables)
//		mysqldump -uoms -poms -h 192.168.0.198 oms tb_biz_refund_record --where="applytime>'2017-04-14 17:19:41'" > C:\Users\Administrator\Desktop\data\tb_biz_refund_record.sql
		partDataTables.each {
			def split=it.split('##')
			def s="$dumpExe $dumpParams -u$dbUser -p$dbPwd -h $dbHost $dbName ${split[0]} --where=\"${split[1]}\" > $sqlDir/${split[0]}.sql"
			println(s)
//			s.execute()
//			printProcess(s.execute())
		}

		println("source $sqlSourceDir/all.sql")
		partDataTables.each {
			println("source $sqlSourceDir/${it.split('##')[0]}.sql")
		}





	}
	


	
	

	
}
