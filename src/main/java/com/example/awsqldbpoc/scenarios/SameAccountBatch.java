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

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	public SameAccountBatch(Ledger ledger) {
		super();
		this.ledger = ledger;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("SameAccountBatch: {} transactions", transactionsCount);

		if (activeProfile.contains("qldb") && transactionBlockSize > 30)
			transactionBlockSize = 30;
		else if (activeProfile.contains("dynamo") && transactionBlockSize > 25)
			transactionBlockSize = 25;

		long currentDate = System.currentTimeMillis();
		String accountId = UUID.randomUUID().toString();
		long elapsedTime;
		long processedItems = 0;
		long blockCount = 0;

		try {
			while (processedItems < transactionsCount) {
				List<Transaction> transactions = new ArrayList<>(transactionsCount);
				long currentAccountDate = System.currentTimeMillis();

				for (int i = 1; i <= transactionBlockSize; i++) {
					transactions.add(new Transaction(accountId, UUID.randomUUID().toString(), LocalDateTime.now(),
							"Transaction Test " + processedItems, BigDecimal.valueOf(1)));
					processedItems++;
				}

				ledger.register(new TransactionBlock(accountId, transactions));

				elapsedTime = (System.currentTimeMillis() - currentAccountDate);

				LOGGER.info("Processed transaction #{} in {} ms. Items: {} - tps: {}", ++blockCount, elapsedTime,
						transactions.size(), 1000.0 / ((double) elapsedTime / (double) transactions.size()));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			double totalTime = System.currentTimeMillis() - currentDate;

			LOGGER.info("Process finished: {} ms - AccountID: {} - AVG TPS: {}", totalTime, accountId,
					1000.0 / (totalTime / (double) transactionsCount));
			super.onApplicationEvent(event);
		}
	}

}