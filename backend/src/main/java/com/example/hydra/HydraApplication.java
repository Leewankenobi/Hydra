package com.example.hydra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HydraApplication {

	public static void main(String[] args) {
		SpringApplication.run(HydraApplication.class, args);
	}

}
