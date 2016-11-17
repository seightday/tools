/**
 * 
 */
package com.seightday.cities;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author heantai
 *
 */
public class H2Table {
	
	private final static Logger log = LoggerFactory
			.getLogger(H2Table.class);
	
	protected H2DB h2db;
	
	protected String createTable;
	
	protected String table;
	
	public String idKey;
	
	public void setH2DB(H2DB h2db) {
		this.h2db=h2db;
	}

	public String getTable() {
		return this.createTable;
	}

	
	private String map2InsertSql(Map<String, String> map) {
		StringBuffer sql = new StringBuffer("INSERT INTO ").append(table);
		StringBuffer keySql = new StringBuffer(" (");
		StringBuffer valueSql = new StringBuffer(" VALUES (");
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			keySql.append(key).append(",");
			valueSql.append("'"+map.get(key)+"'").append(",");
		}
		keySql.deleteCharAt(keySql.length()-1).append(") ");
		valueSql.deleteCharAt(valueSql.length()-1).append(") ");
		return sql.append(keySql).append(valueSql).toString();
	}
	
	
	public void save(Map<String, String> map){
		String insertSql = map2InsertSql(map);
		while(true){//动态增加字段
			try {
				h2db.update(insertSql);
				break;
			} catch (Exception e) {
				String s=e.getMessage();
				if(!s.contains("not found")){
					throw new RuntimeException(e);
				}
				int i1=s.indexOf(" \"");
				int i2=s.indexOf("\" ");
				String column=s.substring(i1+2, i2);
				log.warn("column {} not found",column);
				String sql="ALTER TABLE "+table+" ADD "+column+ " VARCHAR";
				h2db.update(sql);
			}
		}

	}
	
	public List<Map<String, Object>> queryForList(String sql){
		return h2db.queryForList(sql);
	}

}
