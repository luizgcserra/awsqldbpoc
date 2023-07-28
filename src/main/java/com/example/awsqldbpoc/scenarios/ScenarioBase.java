package com.example.awsqldbpoc.scenarios;

import java.time.Duration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public abstract class ScenarioBase implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			System.out.println();
			System.out.println();
			System.out.println("Waiting for metrics to be sent to CloudWatch");
			System.out.println();

			this.sleep();

			System.exit(0);
		} catch (Exception e) {

		}
	}

	private void sleep() throws InterruptedException {
		Thread.sleep((long) Duration.ofSeconds(80).toMillis());
	}

}
