package com.seightday;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

/**
 * http://camel.apache.org/ftp.html
 * http://camel.apache.org/file2.html
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
@Import(FtpFilesImportBeanDefinitionRegistrar.class)
public class FtpFilesDownloadApplication {

	//结合grep与less
	//elk可暂缓
	//elastic search能不能查看某条记录前后1000条的记录
	public static void main(String[] args) {
		SpringApplication.run(FtpFilesDownloadApplication.class, args);
	}

	//以下filter和router通过动态bean进行设置
//	@Bean("omsFilter")
//	public Filter omsFilter(@Value("${oms.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("omsRouter")
//	public Route omsRouter(@Value("${oms.ftp.server.info}")String sftpServer,@Value("${oms.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}
//
//	@Bean("bfsmFilter")
//	public Filter bfsmFilter(@Value("${bfsm.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("bfsmRouter")
//	public Route bfsmRouter(@Value("${bfsm.ftp.server.info}")String sftpServer,@Value("${bfsm.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}
//
//	@Bean("bfssFilter")
//	public Filter bfssFilter(@Value("${bfss.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("bfssRouter")
//	public Route bfssRouter(@Value("${bfss.ftp.server.info}")String sftpServer,@Value("${bfss.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}
//
//	@Bean("tdcmFilter")
//	public Filter tdcmFilter(@Value("${tdcm.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("tdcmRouter")
//	public Route tdcmRouter(@Value("${tdcm.ftp.server.info}")String sftpServer,@Value("${tdcm.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}
//
//	@Bean("tdcsFilter")
//	public Filter tdcsFilter(@Value("${tdcs.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("tdcsRouter")
//	public Route tdcsRouter(@Value("${tdcs.ftp.server.info}")String sftpServer,@Value("${tdcs.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}
//
//	@Bean("smsFilter")
//	public Filter smsFilter(@Value("${sms.local.dir}") String localDir,@Value("${days}") int days){
//		return new Filter(localDir, days);
//	}
//
//	@Bean("smsRouter")
//	public Route smsRouter(@Value("${sms.ftp.server.info}")String sftpServer,@Value("${sms.ftp.local.dir}")String downloadLocation){
//		return new Route(sftpServer,downloadLocation);
//	}

	@Value("${logs.dir}")
	private String logsDir;
	@Value("${logs.unzip.days}")
	private Integer unzipDays;
	@Value("${logs.gz.delete.days}")
	private Integer gzDeleteDays;

	@Scheduled(initialDelay = 1000,fixedDelay = 5000)
	public void unzipLogs(){
		Collection<File> gzLogs = FileUtils.listFiles(new File(logsDir), new String[]{"gz"}, true);
//		gzLogs.forEach(System.out::println);

		gzLogs.forEach(f->{
			try {
				log.info("gz file is {}",f.getAbsolutePath());
				String name = f.getName();
				String[] split = name.split("\\-");
				log.info("split is {}", Arrays.asList(split));
				String date = split[1].replace(".gz", "");
				log.info("date is {}",date);

				LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
				LocalDate now = LocalDate.now();

				LocalDate deleteDay = now.minusDays(gzDeleteDays);
				if(logDate.isBefore(deleteDay)){//.gz文件删除超过配置的天数的文件
					log.info("删除gz文件{}",f.getAbsolutePath());
					f.delete();
					return;
				}

				LocalDate _now = now.minusDays(unzipDays);
				if(logDate.isBefore(_now)){//解压最近天数内的日志
					log.info("logdate is {} days before",unzipDays);
					return;
				}

				String parent = f.getParent();
				log.info("parent  is {}",parent);
				File unzipFile = new File(parent + "\\logs\\"+date+".log");
				if(unzipFile.exists()){
                    log.info("unzipFile exists");
                    return;
                }
                FileUtils.touch(unzipFile);
				GZIPInputStream gzis =new GZIPInputStream(new FileInputStream(f));
				FileOutputStream out =new FileOutputStream(unzipFile);
				IOUtils.copy(gzis,out,1024);
				IOUtils.closeQuietly(gzis);
				IOUtils.closeQuietly(out);
				log.info("unzip gz finish");

			} catch (Exception e) {
				log.error(null,e);
			}
		});

		Collection<File> logs = FileUtils.listFiles(new File(logsDir), new String[]{"log"}, true);
		logs.forEach(f->{//.log文件删除最近天数内的，比解压的多一天，以免刚解压又被删除
			log.info("log file  is {}",f.getAbsolutePath());
			String name = f.getName();
			name = name.replace(".log", "");
			LocalDate logDate = LocalDate.parse(name, DateTimeFormatter.ofPattern("yyyyMMdd"));
			LocalDate now = LocalDate.now();
			LocalDate _now = now.minusDays(unzipDays + 1);
			if(logDate.isBefore(_now)){
				log.info("删除日志文件{}",f.getAbsolutePath());
			    f.delete();
			}

		});
	}


}
