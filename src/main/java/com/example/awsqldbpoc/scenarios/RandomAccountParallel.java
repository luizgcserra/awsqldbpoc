package com.example.awsqldbpoc.scenarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.service.Ledger;

@Profile("RandomAccountParallel")
@Component
public class RandomAccountParallel extends ScenarioBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomAccountParallel.class);

	private AtomicInteger threadCount = new AtomicInteger();

	private final Ledger ledger;
	private final Executor asyncExecutor;

	@Value("${max-threads:10}")
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
		LOGGER.info("RandomAccountParallel: {} transactions", transactionsCount);

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
						ledger.register(new Transaction(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
								LocalDateTime.now(), "Transaction Test " + currentExecution, BigDecimal.valueOf(1)));
					} finally {
						LOGGER.info("Processed transaction #{} in {} ms- Thread: {}", currentExecution,
								(System.currentTimeMillis() - currentExecutionTime), Thread.currentThread().getName());

						threadCount.decrementAndGet();
					}
				});

			}

			while (threadCount.get() > 0) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			double totalTime = System.currentTimeMillis() - currentDate;
			LOGGER.info("Process finished: {} ms - AVG TPS: {}", totalTime,
					1000.0 / (totalTime / (double) transactionsCount));
			super.onApplicationEvent(event);
		}
	}

}
