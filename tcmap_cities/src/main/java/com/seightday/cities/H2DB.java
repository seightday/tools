package com.seightday.cities;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class H2DB {
	
	private final static Logger log = LoggerFactory.getLogger(H2DB.class);
	
	private SimpleJdbcTemplate template;
	
	public H2DB(String dbName,String... tables) {
		String db=PropertyUtil.getClassPath()+"db/"+dbName;
		Properties conProps=new Properties();
		conProps.put("driverClassName", "org.h2.Driver");
		DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:h2:"+db, conProps);
		template = new SimpleJdbcTemplate(dataSource);
		
		File file = new File(db+".mv.db");
		if(file.exists()){
			file.delete();
		}
		
		//初始化数据库
		log.debug("初始化数据库{}，创建表{}",dbName,Arrays.toString(tables));
		for (String table : tables) {
			template.update(table);
		}
		
	}

	public void update(String insertSql) {
		template.update(insertSql);
	}
	
	public List<Map<String, Object>> queryForList(String sql){
		return template.queryForList(sql);
	}



}
