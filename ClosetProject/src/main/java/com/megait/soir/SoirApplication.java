package com.megait.soir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SoirApplication {

	public static void main(String[] args) {

		SpringApplication.run(SoirApplication.class, args);

//		SpringApplication app = new SpringApplication(SoirApplication.class);
//		app.setWebApplicationType(WebApplicationType.NONE);
//		app.run(args);
	}

}
