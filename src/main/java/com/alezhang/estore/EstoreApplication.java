package com.alezhang.estore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class EstoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(EstoreApplication.class, args);
	}
}
