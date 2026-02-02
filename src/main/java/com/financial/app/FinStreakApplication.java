package com.financial.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinStreakApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinStreakApplication.class, args);
	}

}
