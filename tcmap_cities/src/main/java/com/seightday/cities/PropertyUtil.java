/**
 * 
 */
package com.seightday.cities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author heantai
 *
 */
public class PropertyUtil {
	
	
	private final static Logger log = LoggerFactory.getLogger(PropertyUtil.class);
	
	private static String CLASSPATH;
	
	private static Properties PROPERTIES=new Properties();
	
	
	
	static{
		//windows
		CLASSPATH = PropertyUtil.class.getClass().getResource("/").getPath().substring(1);
		
		try {
			//中文及空格处理
			CLASSPATH = URLDecoder.decode(CLASSPATH, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(null, e);
		}
		
		InputStream inputStream =null;
		try {
			inputStream = new FileInputStream(CLASSPATH+"config.properties");
			PROPERTIES.load(new InputStreamReader(inputStream,"UTF-8"));
		} catch (Exception e) {
			log.error("读取配置文件异常"	, e);
		}finally {
			 IOUtils.closeQuietly(inputStream);
		 }
	}
	
	
	public static String get(String key){
		return PROPERTIES.getProperty(key);
	}
	
	public static Integer getAsInt(String key){
		String property = PROPERTIES.getProperty(key);
		if(property==null){
			return null;
		}
		return new Integer(property);
	}

	public static String getClassPath(){
		return CLASSPATH;
	}
	
}
