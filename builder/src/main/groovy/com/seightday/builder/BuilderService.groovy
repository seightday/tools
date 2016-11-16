package com.seightday.builder

import java.util.HashMap;
import java.util.Map;

import groovy.sql.Sql
import groovy.util.logging.Slf4j

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component


@Slf4j
@Component
class BuilderService implements CommandLineRunner{
	
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
	
	
	@Value('${svn.username}')
	String svnUserName
	
	@Value('${svn.password}')
	String svnPassword
	@Value('${project.name}')
	String projectName
	@Value('${project.version}')
	String projectVersion
	@Value('${exclude.files}')
	String exFiles
	@Value('${tomcat.home}')
	String tomcatHome
	
	String basePath
	String headPath
	String diffOutput
	String cp
	
	Sql db
	
	@Override
	public void run(String... arg0) throws Exception {
//		def ant = new AntBuilder()
		
		checkoutOrUpdateSvn()
		
		def builded=build()
		if(!builded){
			log.info('编译失败，请重试')
			return
		}
		
		diff()
		
	}
	
	def diff(){
		diffOutput=diffOutput.replace('\\', '/')
		FileUtils.deleteDirectory(new File(diffOutput))
		def diffProperties="""
#new version
dir1=${headPath.replace('\\', '/')}/build/war
#old version
dir2=${basePath.replace('\\', '/')}/build/war
dest=$diffOutput

spring.datasource.url=$dbUrl
spring.datasource.username=$dbUser
spring.datasource.password=$dbPwd

logging.file=$diffOutput/log.txt

# won't be copy to the dest dir ,will list in the summary.txt
exclude.files=$exFiles
"""
		//更新ProjectIncrementalUpgrade配置
		def p = new File("$cp/ProjectIncrementalUpgrade/application.properties")
		
		FileUtils.write(p, diffProperties, 'UTF-8', false)

		def process = "cmd /c start $cp/ProjectIncrementalUpgrade/bin/startup.bat".execute()
		printProcess(process)
	}
	
	boolean build(){
		Map<String, String> map=new HashMap<String, String>();
		map.put("tomcat.home", tomcatHome);
		
		try {
			map.put("project.dir", basePath);
			log.info("map is $map")
			def result=Ant.execute("$cp/build.xml", "warDir", map);
			if(!result.success){
				return false
			}
			map.put("project.dir", headPath);
			log.info("map is $map")
			result=Ant.execute("$cp/build.xml", "warDir", map);
			return result.success
		} catch (Exception e) {
			log.error(null,e)
			return false
		}
	}

	private void checkoutOrUpdateSvn() {
		cp = getClasspath()
		def file = new File(cp)
		def parent = file.getParent()
		basePath="$parent/${projectName}_${projectVersion}_src/base"
		headPath="$parent/${projectName}_${projectVersion}_src/head"
		diffOutput="$parent/${projectName}_${projectVersion}_src/output"
		log.info("basePath is $basePath,headPath is $headPath,diffOutput is $diffOutput")
		def baseDir = new File(basePath)
		if(!baseDir.exists()){
			baseDir.mkdirs()
			checkout("$svnBranchUrl@$svnBranchBase", basePath)
		}
		def headDir = new File(headPath)
		if(!headDir.exists()){
			headDir.mkdirs()
			checkout("$svnBranchUrl@HEAD", headPath)
		}else{
			update(headPath)
		}
	}
	
	public void checkout(String url,String path){
		log.info("checkout url is $url,path is $path")
		def cmd="${svnExe} checkout $url $path --username ${svnUserName} --password ${svnPassword} "
		def p = cmd.execute()
		
		printProcess(p)
	}
	public void update(String path){
		log.info("update path is $path")
		//避免restore class文件
		def cmd="$svnExe pset svn:ignore \"*.class\" & ${svnExe} update $path --username ${svnUserName} --password ${svnPassword} "
		def p = cmd.execute()
		
		printProcess(p)
	}
	
	
	
	
	
	public void printProcess(Process p){
		log.info("printProcess")
		def line=null
		
		def reader = new BufferedReader(new InputStreamReader(p.getInputStream(), 'gbk'))
		while((line=reader.readLine())!=null){
			log.info(line)
		}
	}

	
	public String getClasspath(){
		String classPath
		URL resource = this.getClass().getClassLoader().getResource("/");//web app
		if(resource==null){
			resource = this.getClass().getResource("/");//jar app
		}
		
		if(System.getProperty("os.name").toLowerCase().indexOf("windows")>=0){
			classPath = resource.getPath().substring(1);
		}else{
			classPath = resource.getPath();
		}
		
		
		try {
			//中文及空格处理
			classPath = URLDecoder.decode(classPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("路径中文处理失败",e);
		}
		
		log.info("classPath:{}",classPath);
		
		return classPath;
	}
}
