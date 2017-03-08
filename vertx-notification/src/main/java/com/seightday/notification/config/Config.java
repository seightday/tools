package com.seightday.notification.config;

import com.github.aesteve.vertx.nubes.VertxNubes;
import com.seightday.notification.service.MailService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config{

	@Bean
	public Vertx getVertxInstance() {
		return Vertx.vertx();
	}
	
	@Bean
	public VertxNubes vertxNubes(Vertx vertx){
		JsonObject config = new JsonObject();
		config.put("host", "localhost");
		config.put("port", 8080);
		JsonArray cp = new JsonArray();
		cp.add("com.seightday.notification.controller");
		config.put("controller-packages", cp);

		JsonObject services=new JsonObject();
		services.put(MailService.NAME, MailService.class.getName());
		config.put("services", services);

		VertxNubes nubes = new VertxNubes(vertx, config);
		nubes.bootstrap(res -> {
		  if (res.succeeded()) {
		    Router yourRouter = res.result();
		    System.out.println("Everything's ready");
		    vertx.createHttpServer().requestHandler(yourRouter::accept).listen(8080);
		  } else {
		    System.err.println("Something went wrong");
		    res.cause().printStackTrace();
		  }
		});
		return nubes;
	}

}
