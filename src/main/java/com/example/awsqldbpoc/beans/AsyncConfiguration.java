package com.example.awsqldbpoc.beans;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

	@Bean(destroyMethod = "shutdown")
	@Primary
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(1500);
		threadPoolTaskExecutor.setCorePoolSize(100);
		threadPoolTaskExecutor.setQueueCapacity(10);
		threadPoolTaskExecutor.setThreadNamePrefix("transaction-");
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.setDaemon(true);
		threadPoolTaskExecutor.setAwaitTerminationSeconds(10);
		threadPoolTaskExecutor.initialize();

		return threadPoolTaskExecutor;
	}
}
