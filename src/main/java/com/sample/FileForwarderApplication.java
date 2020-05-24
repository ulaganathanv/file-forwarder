package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileForwarderApplication {
	private static ApplicationContext applicationContext;

	public static void main (String[] args){

		applicationContext = SpringApplication.run(FileForwarderApplication.class, args);
	}
}
