package com.ivan_degtev.documentaccounting2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DocumentAccounting2Application {
	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication.run(DocumentAccounting2Application.class, args);
	}
}
