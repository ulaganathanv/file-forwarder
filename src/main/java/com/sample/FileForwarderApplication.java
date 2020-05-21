package com.sample;

import com.sample.util.PropertyReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class FileForwarderApplication {

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
		return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				System.out.println(PropertyReader.getInstance().getProperty("INCOMING_DIR"));
			}
		};
	}

	public static void main(String[] args) {

		SpringApplication.run(FileForwarderApplication.class, args);
	}

}
