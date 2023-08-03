package com.example.awsqldbpoc.scenarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.service.Ledger;

@Profile("SameAccountSerial")
@Component
public class SameAccountSerial extends ScenarioBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SameAccountSerial.class);

	private final Ledger ledger;

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	public SameAccountSerial(Ledger ledger) {
		super();
		this.ledger = ledger;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("SameAccountSerial: {} transactions", transactionsCount);
		String accountId = UUID.randomUUID().toString();
		long currentDate = System.currentTimeMillis();

		try {
			for (int i = 1; i <= transactionsCount; i++) {
				long currentAccountDate = System.currentTimeMillis();
				Transaction transaction = new Transaction(accountId, UUID.randomUUID().toString(), LocalDateTime.now(),
						"Transaction Test " + i, BigDecimal.valueOf(1));

				ledger.register(transaction);

				LOGGER.info("Processed transaction #{} in {} ms - UniqueId: {}", i,
						(System.currentTimeMillis() - currentAccountDate), transaction.getUniqueId());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LOGGER.info("Process finished: {} ms", (System.currentTimeMillis() - currentDate));
			super.onApplicationEvent(event);
		}
	}

}
