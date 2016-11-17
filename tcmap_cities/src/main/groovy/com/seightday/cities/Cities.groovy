package com.seightday.cities

import groovy.sql.Sql;

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements

class Cities {

	//TODO 为测试，在sys_area增加了code与name的唯一键
	
	private static Map<String, String> cookies;
	
	static String dbUrl="jdbc:mysql://localhost:3306/dmp?useUnicode=true&characterEncoding=utf-8";
	static String dbUser="root";
	static String dbPwd="123456";
	static String dbDriver='com.mysql.jdbc.Driver'
	
	static String china='10'
	
//	private static final String serverurl = "http://127.0.0.1:8080/platform"
	private static final String serverurl = "http://192.168.10.118:8088/com.thinkgem.jeesite.jeesite-1.2.6"
	
	
	private static final String citiesPrefixUrl = "http://www.tcmap.com.cn"
	
	//TODO 这几个地方单独再改code,不一定，直接按照市来对待区一样的，将街道删除
	private static final List<String> zhixiashi=['澳门','香港特别行政区','台湾','重庆','上海','天津','北京']
	
	static main(args) {
		//login();
		
		def ps = getPronvince()
		
		//循环添加省
		for(p in ps){
			def name = p.get("name")
			addProvince(p)
		}
		
		
		//循环添加市及区
		for (p in ps) {
			def name = p.get('name')
			def href = p.get('href')
			
			getMunicipal(href)
			
			if(zhixiashi.contains(name)){

				//按添加区处理
				//				println "exclude ${name}"
			}else{
			//添加市，并添加区
			}
			
			println '\r\n\r\n\r\n'
		}
		
		//getMunicipal('/ningxia/')
		
	}
	
	static List<Municipal> getMunicipal(String url){
		def result=[]
		
		
		def mUrl=citiesPrefixUrl+url
		
		Connection connect = Jsoup.connect(mUrl)
		Document d = connect.get()
		Elements es = d.select('div#page_left table tr')//tr中的第一列为市，最后 一列为区 TODO
		
		Iterator<Element> iterator = es.iterator()
		
		while (iterator.hasNext()) {
			Element tr = iterator.next()
			Elements children = tr.children()
			
			if(!children.toString().startsWith('<td nowrap>')){
				continue
			}
			
			println "有效记录 ${children}"
			
			println '市：'+children.eq(0).toString()
			println '区：'+children.eq(5).toString()
			
			
			def ma = children.eq(0).select("a")
			println "m a:"+ma
			
			def munipal=new Municipal(name: ma.first().html() )
			
			
			
			
			println "d a:"+children.eq(5).select("a")
			
			Iterator<Element> da=children.eq(5).select("a").iterator()
			
			while(da.hasNext()){
				Element a=da.next()
				munipal.addDistrict(a.html())
			}
			
			println "munipal is ${munipal.name}  districts is ${munipal.districts}"
			
			result.add(munipal)
			
		}
		
		println "result is ${result}"
		result
	}
	
	static List<Map<String, String>> getPronvince(){
		def pUrl=citiesPrefixUrl+'/list/jiancheng_list.html'
		Connection connect = Jsoup.connect(pUrl)
		Document d = connect.get()
		Elements es = d.select('div#page_left a')
		Iterator<Element> iterator = es.iterator()
		
		def ps=[]
		while (iterator.hasNext()) {
			def next = iterator.next()
			String href = next.attr('href')
			String html = next.html()
			
			
			if(html.endsWith("省")){
				html=html.substring(0, html.length()-1)
			}
			
			println "href is ${href},name is ${html}"
			ps.add([name:html,href:href]) 
		}
		
		println ps
		ps
	}
	
	static addProvince(Map<String, String> p){
		String name=p.get("name")
		String href=p.get("href")
		
		def db = Sql.newInstance(dbUrl, dbUser, dbPwd, dbDriver)
		def rows = db.rows("select * from sys_area where name=${name} and type='2'")
		println "rows ${rows}"
		if(!rows.isEmpty()){
			println "province ${name} exist"
			return
		}
		
		//查询parent_id为1，根据code降序排序的第一个，如果是空则没添加为省，从10开始，不为空则在最大的基础加1
		
		rows=db.rows("select * from sys_area where parent_id='1' order by code desc limit 1")
		
		String prefix="10"
		if(rows.empty){
			prefix="1010"
		}else{
			def r = rows.get(0)
			println "name is ${r.name},code is ${r.code}"
			String code=r.code
			println "code is ${code}"
			code=code.substring(2, 4)
			println "code is ${code}"
			int i=Integer.valueOf(code)
			prefix+=++i
		}
		prefix+="0000"
		println "prefix is ${prefix}"
		
		def data=['code':prefix,name:name,'parent.id':'1',type:'2']
		println "data is ${data}"
		
		Connection connect = Jsoup.connect(serverurl+"/a/sys/area/save").cookies(cookies);
		connect.data(data)
		connect.post();
		
		//返回生成的id，给市当parentid
		rows=db.rows("select * from sys_area where parent_id='1' order by code desc limit 1")
		String id=rows.get(0).get("id")
		String code = rows.get(0).get('code')
		
		
	}
	
	static addMunipal(String name,String parentId,String prefix){
		def db = Sql.newInstance(dbUrl, dbUser, dbPwd, dbDriver)
		def rows = db.rows("select * from sys_area where name=${name} and type='3'")
		println "rows ${rows}"
		if(!rows.isEmpty()){
			println "province ${name} exist"
			return
		}
		
		//查询parent_id为1，根据code降序排序的第一个，如果是空则没添加为省，从10开始，不为空则在最大的基础加1
		
		rows=db.rows("select * from sys_area where parent_id='${parentId}' order by code desc limit 1")
		
		if(rows.empty){
			prefix+="10"
		}else{
			def r = rows.get(0)
			println "name is ${r.name},code is ${r.code}"
			String code=r.code
			println "code is ${code}"
			code=code.substring(4, 6)
			println "code is ${code}"
			int i=Integer.valueOf(code)
			prefix+=++i
		}
		prefix+="00"
		println "prefix is ${prefix}"
		
		def data=['code':prefix,name:name,'parent.id':parentId,type:'3']
		println "data is ${data}"
		
		Connection connect = Jsoup.connect(serverurl+"/a/sys/area/save").cookies(cookies);
		connect.data(data)
		Document document = connect.post();
	}
	
	private static void login() throws IOException {
		Connection connect = Jsoup.connect(serverurl+"/a/login");
		connect.data("password", "admin");
		connect.data("username", "admin");
		Response response = connect.method(Method.POST).execute();
		cookies = response.cookies();
		println cookies
	}

}
