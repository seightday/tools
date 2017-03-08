package com.seightday.versioncompare

import groovy.sql.Sql
import groovy.util.logging.Slf4j

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Slf4j
@Component
class VersionCompareService implements CommandLineRunner{
	
	@Value('${jdbc.password}')
	String dbPwd
	
	@Value('${jdbc.username}')
	String dbUser
	
	@Value('${jdbc.url}')
	String dbUrl
	
	@Value('${jdbc.driver}')
	String dbDriver
	
	@Value('${svn.exe}')
	String svnExe
	
	@Value('${svn.branch.url}')
	String svnBranchUrl
	
	@Value('${svn.branch.base}')
	String svnBranchBase
	
	@Value('${svn.trunk.url}')
	String svnTrunkUrl
	
	@Value('${svn.trunk.base}')
	String svnTrunkBase
	
	@Value('${svn.username}')
	String svnUserName
	
	@Value('${svn.password}')
	String svnPassword
	
	Sql db
	
	@Override
	public void run(String... arg0) throws Exception {
		db=Sql.newInstance(dbUrl,dbUser, dbPwd, dbDriver)
		def diffs = generateDiff(svnBranchUrl, svnBranchBase)
//		def changed="""
//"""
//		diffs=generateDiff(changed)
		saveDiff(diffs, "path1")
		diffs=generateDiff(svnTrunkUrl, svnTrunkBase)
//		changed="""
//"""
//		diffs=generateDiff(changed)
		saveDiff(diffs, "path2")
		compareDiff()
		
	}

	public List<String> generateDiff(String changed){
		def split = changed.split('\n')
		return Arrays.asList(split)
	}
	

	public void compareDiff(){
		log.info "----------------- compare diff ----------------"
		db.eachRow('select distinct(path1) from ( select t1.`PATH` as path1,t2.`PATH` as path2 from path1 t1 left join path2 t2 on t1.`PATH`=t2.`PATH` where t2.`PATH` is not null ) tmp') {
			println  "${it}"
		}
	}
	
	public void saveDiff(List<String> diffs,String table){
		log.info "save to ${table}"
		db.execute("truncate table "+table)
		diffs.each {
			println it
			db.execute("insert into "+table+" (path) values ('"+it+"')")
			//def _sql="insert into ${table} (path) values (${it})"//执行出错
		}
	}
	
	public List<String> generateDiff(String svnUrl,String svnBase){
		log.info "generateDiff svnUrl is ${svnUrl}, svnBase is ${svnBase}"
		def result=[]
		def cmd="${svnExe} diff --old ${svnUrl}@${svnBase} --new ${svnUrl}@HEAD --username ${svnUserName} --password ${svnPassword} --summarize"
		log.info("cmd is ${cmd}")
		def p = cmd.execute()
		log.debug("printProcess")
		def line=null
		
		def reader = new BufferedReader(new InputStreamReader(p.getInputStream(), 'gbk'))
		while((line=reader.readLine())!=null){
			log.info(line)
			line=line.replaceFirst(" ", "")
			line=line.replaceFirst(/\s*\w\s*/, '')
			line=line.replace("${svnUrl}", '')
			log.info(line)
			if(line){//忽略空字符串
				result.add(line)
			}
		}
		return result
	}
	

}
