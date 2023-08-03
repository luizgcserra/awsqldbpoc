package com.example.awsqldbpoc.scenarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.domain.TransactionBlock;
import com.example.awsqldbpoc.service.Ledger;

@Profile("SameAccountBatch")
@Component
public class SameAccountBatch extends ScenarioBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SameAccountBatch.class);

	private final Ledger ledger;

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	@Value("${transactions-block-size:30}")
	private int transactionBlockSize;

	public SameAccountBatch(Ledger ledger) {
		super();
		this.ledger = ledger;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("SameAccountBatch: {} transactions", transactionsCount);

		long currentDate = System.currentTimeMillis();
		String accountId = UUID.randomUUID().toString();
		long elapsedTime;

		try {
			for (int j = 0; j < (int) Math.round(((double) transactionsCount / transactionBlockSize) + 0.5d); j++) {
				List<Transaction> transactions = new ArrayList<>(transactionsCount);
				long currentAccountDate = System.currentTimeMillis();

				for (int i = 1; i <= transactionBlockSize; i++) {
					transactions.add(new Transaction(accountId, String.valueOf(i), LocalDateTime.now(),
							"Transaction Test " + i, BigDecimal.valueOf(1)));

				}

				ledger.registerTransaction(new TransactionBlock(accountId, transactions));

				elapsedTime = (System.currentTimeMillis() - currentAccountDate);

				LOGGER.info("Processed transaction #{} in {} ms. Items: {} - rps: {}", j, elapsedTime,
						transactions.size(), 1000.0 / ((double) elapsedTime / (double) transactions.size()));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LOGGER.info("Process finished: {} ms - AccountID: {}", (System.currentTimeMillis() - currentDate),
					accountId);
			super.onApplicationEvent(event);
		}
	}

}