package com.example.awsqldbpoc.scenarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.example.awsqldbpoc.domain.TransactionBlock;
import com.example.awsqldbpoc.service.Ledger;

@Profile("SameAccountBatchParallel")
@Component
public class SameAccountBatchParallel extends ScenarioBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SameAccountBatchParallel.class);

	private final Ledger ledger;
	private final Executor asyncExecutor;
	private final AtomicInteger threadCount = new AtomicInteger();

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	@Value("${transactions-count:30}")
	private int transactionBlockSize;

	@Value("${max-threads:100}")
	private int maxThreads;

	public SameAccountBatchParallel(Ledger ledger, Executor asyncExecutor) {
		super();
		this.ledger = ledger;
		this.asyncExecutor = asyncExecutor;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("SameAccountBatchParallel: {} transactions", transactionsCount);

		long currentDate = System.currentTimeMillis();
		String accountId = UUID.randomUUID().toString();

		try {
			for (int j = 0; j < (int) Math.round(((double) transactionsCount / transactionBlockSize) + 0.5d); j++) {
				threadCount.incrementAndGet();

				int currentExecution = j;

				while (threadCount.get() >= maxThreads) {
					Thread.sleep(20);
				}

				asyncExecutor.execute(() -> {
					long currentExecutionTime = System.currentTimeMillis();
					List<Transaction> transactions = new ArrayList<>(transactionsCount);

					try {
						for (int i = 1; i <= transactionBlockSize; i++) {
							transactions.add(new Transaction(accountId, String.valueOf(i), LocalDateTime.now(),
									"Transaction Test " + i, BigDecimal.valueOf(1)));

						}

						ledger.registerTransaction(new TransactionBlock(accountId, transactions));

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
