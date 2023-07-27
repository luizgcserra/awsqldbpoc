package com.example.awsqldbpoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.service.Ledger;

@Profile("RandomAccountParallel")
@Component
public class RandomAccountParallel implements ApplicationListener<ApplicationReadyEvent> {

	private AtomicInteger threadCount = new AtomicInteger();

	private final Ledger ledger;
	private final Executor asyncExecutor;

	@Value("${max-threads:500}")
	private int maxThreads;
	
	@Value("${transactions-count:2000}")
	private int transactionsCount;

	public RandomAccountParallel(Ledger ledger, Executor asyncExecutor) {
		super();
		this.ledger = ledger;
		this.asyncExecutor = asyncExecutor;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("RandomAccountParallel");

		long currentDate = System.currentTimeMillis();

		try {
			for (int i = 1; i <= transactionsCount; i++) {
				threadCount.incrementAndGet();
				int currentExecution = i;

				while (threadCount.get() >= maxThreads) {
					Thread.sleep(20);
				}

				asyncExecutor.execute(() -> {
					long currentExecutionTime = System.currentTimeMillis();

					try {
						ledger.registerTransaction(new Transaction(UUID.randomUUID().toString(),
								String.valueOf(currentExecution), LocalDateTime.now(),
								"Transaction Test " + currentExecution, BigDecimal.valueOf(1)));
					} finally {
						System.out.println("Processed transaction #" + currentExecution + " in "
								+ (System.currentTimeMillis() - currentExecutionTime) + " ms - Thread: "
								+ Thread.currentThread().getName());

						threadCount.decrementAndGet();
					}
				});

			}

			while (threadCount.get() > 0) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			System.out.println("Process finished: " + (System.currentTimeMillis() - currentDate) + " ms");
		}
	}

}
