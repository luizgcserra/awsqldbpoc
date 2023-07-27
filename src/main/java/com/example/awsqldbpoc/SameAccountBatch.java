package com.example.awsqldbpoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.domain.TransactionBlock;
import com.example.awsqldbpoc.repository.QldbUnitOfWork;
import com.example.awsqldbpoc.service.Ledger;

@Profile("SameAccountBatch")
@Component
public class SameAccountBatch implements ApplicationListener<ApplicationReadyEvent> {

	private final Ledger ledger;

	@Value("${transactions-count:2000}")
	private int transactionsCount;

	@Value("${transactions-count:30}")
	private int transactionBlockSize;

	public SameAccountBatch(Ledger ledger, QldbUnitOfWork contextRepository) {
		super();
		this.ledger = ledger;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("SameAccountBatch");

		long currentDate = System.currentTimeMillis();

		try {
			for (int j = 0; j < transactionsCount; j++) {
				String accountId = UUID.randomUUID().toString();
				List<Transaction> transactions = new ArrayList<>(transactionsCount);
				long currentAccountDate = System.currentTimeMillis();

				for (int i = 1; i <= transactionBlockSize; i++) {

					transactions.add(new Transaction(accountId, String.valueOf(i), LocalDateTime.now(),
							"Transaction Test " + i, BigDecimal.valueOf(1)));

				}

				ledger.registerTransaction(new TransactionBlock(accountId, transactions));

				System.out.println("Processed transaction #" + j + " in "
						+ (System.currentTimeMillis() - currentAccountDate) + " ms");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			System.out.println("Process finished: " + (System.currentTimeMillis() - currentDate) + " ms");
		}
	}

}
