package com.mli.discord;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mli.discord.module.*.dao")
@SpringBootApplication
public class ApiSpringBoot2Application {
	public static void main(String[] args) {
		SpringApplication.run(ApiSpringBoot2Application.class, args);
	}

}
