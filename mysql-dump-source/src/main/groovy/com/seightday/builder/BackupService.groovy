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
	String[] ignoreTables
	@Value('${db.name}')
	String dbName
	@Value('${db.host}')
	String dbHost
	@Value('${sql.dir}')
	String sqlDir
	@Value('${sql.source.dir}')
	String sqlSourceDir
	@Value('${mysqldump}')
	String mysqldump
	
	@Override
	public void run(String... arg0) throws Exception {
		StringBuilder sb=new StringBuilder();
		ignoreTables.each {
			sb.append(" --ignore-table=${dbName}.$it ")
		}
		def dump="$mysqldump -u$dbUser -p$dbPwd -h $dbHost $dbName $sb > $sqlDir/all.sql"
		println(dump)
//		dump.execute()
//		printProcess(dump.execute())
		//mysqldump -uoms -poms -h 192.168.0.198 oms --ignore-table=oms.tb_clear_paydata_detail --ignore-table=oms.tb_biz_refund_record > C:\Users\Administrator\Desktop\data\ignore.sql
//		print(ignoreTables)
//		mysqldump -uoms -poms -h 192.168.0.198 oms tb_biz_refund_record --where="applytime>'2017-04-14 17:19:41'" > C:\Users\Administrator\Desktop\data\tb_biz_refund_record.sql
		partDataTables.each {
			def split=it.split('##')
			def s="$mysqldump -u$dbUser -p$dbPwd -h $dbHost $dbName ${split[0]} --where=\"${split[1]}\" > $sqlDir/${split[0]}.sql"
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
