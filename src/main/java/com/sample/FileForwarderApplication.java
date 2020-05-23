package com.sample;

import com.sample.listener.DirectoryListener;
import com.sample.service.AmazonClient;
import com.sample.util.PropertyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class FileForwarderApplication {
	private static ApplicationContext applicationContext;

//	@Autowired
//	private DirectoryListener directoryListener;

	@Autowired
	private  AmazonClient amazonClient;

	@Bean
	public TaskExecutor taskExecutor() {

		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	public ApplicationRunner scheduleApplication(TaskExecutor executor) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				executor.execute(new DirectoryListener(amazonClient));
			}
		};
	}

//	@Bean
//	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
//		return new CommandLineRunner() {
//			public void run(String... args) throws Exception {
//				System.out.println(PropertyReader.getInstance().getProperty("INCOMING_DIR"));
//				amazonClient.listBuckets();
//				String fileName = PropertyReader.getInstance().getProperty("INCOMING_DIR") + "/sample.txt";
//				amazonClient.uploadObject("incoming-files-directory", "sample", fileName);
//				System.out.println("Done");
//
//				executor.execute(directoryListener.listen());
//			}
//		};
//	}

	public static void main (String[] args){

		applicationContext = SpringApplication.run(FileForwarderApplication.class, args);
	}
}
