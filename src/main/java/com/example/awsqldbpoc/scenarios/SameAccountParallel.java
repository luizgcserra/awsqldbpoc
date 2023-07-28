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

@Profile("SameAccountParallel")
@Component
public class SameAccountParallel extends ScenarioBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SameAccountParallel.class);

	private final AtomicInteger threadCount = new AtomicInteger();
	private final Ledger ledger;
	private final Executor asyncExecutor;

	@Value("${max-threads:100}")
	private int maxThreads;

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	public SameAccountParallel(Ledger ledger, Executor asyncExecutor) {
		super();
		this.ledger = ledger;
		this.asyncExecutor = asyncExecutor;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("SameAccountParallel: {} transactions", transactionsCount);

		long currentDate = System.currentTimeMillis();
		String accountId = UUID.randomUUID().toString();

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
						ledger.registerTransaction(new Transaction(accountId, String.valueOf(currentExecution),
								LocalDateTime.now(), "Transaction Test " + currentExecution, BigDecimal.valueOf(1)));
					} finally {
						LOGGER.info("Processed transaction #{} in {} ms - Thread: {}", currentExecution,
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
			LOGGER.info("Process finished: {} ms", (System.currentTimeMillis() - currentDate));
			super.onApplicationEvent(event);
		}
	}

}
