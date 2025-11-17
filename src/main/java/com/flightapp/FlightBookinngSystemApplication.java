package com.flightapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FlightBookinngSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightBookinngSystemApplication.class, args);
	}

}
