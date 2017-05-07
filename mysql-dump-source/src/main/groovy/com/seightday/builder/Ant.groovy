package com.seightday.builder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ant {
	
	
	private final static Logger log = LoggerFactory.getLogger(Ant.class);
	
	public static Result execute(String file,String target,Map<String, String> props,String... keys) {
		File buildFile = new File(file);

		Project p = new Project();
		if(props!=null&&!props.isEmpty()){
			Iterator<String> iterator = props.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				p.setProperty(key, props.get(key));
			}
		}

		DefaultLogger consoleLogger = new DefaultLogger();
		
		ByteArrayOutputStream byteArrayOutputStreamErr = new ByteArrayOutputStream();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStreamErr = new PrintStream(byteArrayOutputStreamErr);
		PrintStream printStream = new PrintStream(byteArrayOutputStream);

		consoleLogger.setErrorPrintStream(printStreamErr);
		consoleLogger.setOutputPrintStream(printStream);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

		p.addBuildListener(consoleLogger);

		Result result = new Result();
		try {
			p.fireBuildStarted();
			p.init();

			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(p, buildFile);

			if(target==null){
				target=p.getDefaultTarget();
			}
			p.executeTarget(target);

			p.fireBuildFinished(null);
			result.success=true;
			result.msg=new String(byteArrayOutputStream.toByteArray());
		} catch (BuildException be) {
			log.error(null, be);
			p.fireBuildFinished(be);
			result.msg=new String(byteArrayOutputStream.toByteArray())+new String(byteArrayOutputStreamErr.toByteArray());
		}
		
		if(keys!=null&&keys.length>0){
			for (String key : keys) {
				result.put(key, p.getProperty(key));
			}
			log.debug("result map:{}",result.map);
		}
		
		log.info("ant task result {},{}",result.success,result.msg);
	
		return result;
	}
	
	public static Result execute(String file,String target) {
		return execute(file, target, null);
	}
	
	public static Result executeDefaul(String file) {
		return execute(file, null);
	}

	//将数据输出在echo信息中进行获取
	public static String getEcho(String s){
		if(s==null||s.trim().isEmpty()){
			return "";
		}
		
		String[] split = s.split("\r\n");
		for (String string : split) {
			if(string.contains("[echo]")){
				return string.replace("[echo]", "").trim();
			}
		}
		return "";
	}
	
	static class Result{
		boolean success;
		String msg;
		Map<String, String> map=new HashMap<String, String>();
		public String getValue(String key){
			return map.get(key);
		}
		public void put(String key,String value){
			map.put(key, value);
		}
	}
}
