package com.example.awsqldbpoc.scenarios;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public abstract class ScenarioBase implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			this.sleep(2);

			System.out.println();
			System.out.println();
			System.out.println("Waiting for metrics to be sent to CloudWatch");
			System.out.println();

			this.sleep(80);

			System.exit(0);
		} catch (Exception e) {

		}
	}

	private void sleep(long seconds) throws InterruptedException {
		Thread.sleep(seconds * 1000);
	}

}
