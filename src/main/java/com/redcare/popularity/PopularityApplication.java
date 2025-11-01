package com.redcare.popularity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PopularityApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopularityApplication.class, args);
	}

}
