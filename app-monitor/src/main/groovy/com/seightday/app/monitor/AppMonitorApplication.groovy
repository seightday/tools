package com.seightday.app.monitor

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AppMonitorApplication {

	static void main(String[] args) {
		SpringApplication.run AppMonitorApplication, args
	}

}
