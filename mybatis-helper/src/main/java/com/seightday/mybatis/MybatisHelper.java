package com.seightday.mybatis;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;
import org.apache.ibatis.session.Configuration;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MybatisHelper {
	
	private Configuration configuration;

	public MybatisHelper(String configXml) {
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(configXml);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		configuration=new XMLConfigBuilder(inputStream).parse();
	}

	/**
	 * 生成PreparedSql及对应的参数List
	 * @param sqlId
	 * @param params
	 * @return
	 */
	public Pair<String, List<Object>> build(String sqlId, Object params){
		MappedStatement mappedStatement = configuration.getMappedStatement(sqlId);//查找不到时会报非法参数异常

		BoundSql boundSql = mappedStatement.getBoundSql(params);

		//void org.apache.ibatis.scripting.defaults.DefaultParameterHandler.setParameters(PreparedStatement ps)

		List<Object> parameters=new ArrayList<Object>();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			try {
				for (int i = 0; i < parameterMappings.size(); i++) {
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        String propertyName = parameterMapping.getProperty();
                        parameters.add(Ognl.getValue(propertyName, params));
                    }
                }
			} catch (OgnlException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return Pair.with(boundSql.getSql(), parameters);
	}


}
