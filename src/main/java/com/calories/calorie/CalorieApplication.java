package com.calories.calorie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CalorieApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalorieApplication.class, args);
	}

}
