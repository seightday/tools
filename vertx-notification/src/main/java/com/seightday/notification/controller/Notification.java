package com.seightday.notification.controller;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.seightday.notification.service.MailService;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class Notification {
	
	private static final Logger log = LoggerFactory.getLogger(Notification.class);

	//跨域问题 http://www.cnblogs.com/amylis_chen/p/4703735.html
	@GET("/sendEmail")//缺少参数是可能出现Resource Not Found的错误
	public void sendEmail(@Param("content") String content,@Param("callback") String callback, RoutingContext rc) {
		log.info("content is {},callback is {}",content,callback);
		rc.vertx().eventBus().send(MailService.sendMail, content, reply->{
			//String result=callback+"({errCode:\"00\", errMsg:\"success\"})";
			String result= (String) reply.result().body();
			result=callback+"("+result+")";
			rc.response().putHeader("Content-Length",String.valueOf(result.length()));
			rc.response().write(result);
			rc.next();
			log.info("response ...");
		});
	}

}
