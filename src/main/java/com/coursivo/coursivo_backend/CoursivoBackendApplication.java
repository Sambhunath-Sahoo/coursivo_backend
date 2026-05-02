package com.coursivo.coursivo_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CoursivoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoursivoBackendApplication.class, args);
	}

}
