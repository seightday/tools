package com.seightday.notification.service;

import com.github.aesteve.vertx.nubes.annotations.services.Consumer;
import com.github.aesteve.vertx.nubes.services.Service;
import com.seightday.notification.config.SpringContextHolder;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailService implements Service{
	
	public final static String NAME="MailService";

	public final static String sendMail="sendMail";

	private JavaMailSender mailSender= SpringContextHolder.getBean("mailSender");


	@Override
	public void init(Vertx vertx, JsonObject config) {

	}

	//start与stop示调用future.complete(),系统直接关闭了
	@Override
	public void start(Future<Void> future) {
		future.complete();
	}
	@Override
	public void stop(Future<Void> future) {
		future.complete();
	}
	

	@Consumer(sendMail)
	public void sendMail(Message<String> msg) {
		String content=msg.body();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("");
		message.setTo("");
		message.setSubject("notice");
		message.setText(content);
		mailSender.send(message);
		msg.reply("{errCode:\"00\", errMsg:\"success\"}");
	}

}
