package com.seightday.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NotificationApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(NotificationApplication.class, args);
		String[] names = context.getBeanDefinitionNames();
		for (String name:names) {
			System.out.println("name is "+name);
		}
	}
	

}
