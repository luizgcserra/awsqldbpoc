package com.example.awsqldbpoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.repository.QldbUnitOfWork;
import com.example.awsqldbpoc.service.Ledger;

@Profile("SameAccountSerial")
@Component
public class SameAccountSerial implements ApplicationListener<ApplicationReadyEvent> {

	private final Ledger ledger;

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	public SameAccountSerial(Ledger ledger, QldbUnitOfWork contextRepository) {
		super();
		this.ledger = ledger;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("SameAccountSerial");
		String accountId = UUID.randomUUID().toString();
		long currentDate = System.currentTimeMillis();

		try {
			for (int i = 1; i <= transactionsCount; i++) {

				ledger.registerTransaction(new Transaction(accountId, String.valueOf(i), LocalDateTime.now(),
						"Transaction Test " + i, BigDecimal.valueOf(1)));

				System.out.println(
						"Processed transaction #" + i + " in " + (System.currentTimeMillis() - currentDate) + " ms");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			System.out.println("Process finished: " + (System.currentTimeMillis() - currentDate) + " ms");
		}
	}

}
