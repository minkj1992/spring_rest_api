package com.minkj1992.spring_rest_api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestApiApplication.class, args);
	}


	/**
	 * 공용으로 쓸 수 있는 Bean
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
