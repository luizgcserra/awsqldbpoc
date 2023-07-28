package com.example.awsqldbpoc.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.UnitOfWork;

import io.micrometer.core.annotation.Timed;

@Service
public class Ledger {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ledger.class);

	private final UnitOfWork unitOfWork;

	public Ledger(UnitOfWork unitOfWork) {
		super();
		this.unitOfWork = unitOfWork;
	}

	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public Balance registerTransaction(Transaction transaction) {
		return this.unitOfWork.execute(context -> {
			try {
				context.transactionRepository()
						.registerTransaction(new TransactionModel(transaction.getAccountId(),
								transaction.getTransactionId(), transaction.getUniqueId(),
								ZonedDateTime.of(transaction.getTransactionDate(), ZoneId.systemDefault()).toInstant()
										.toEpochMilli(),
								transaction.getDescription(), transaction.getTransactionAmount()));

				BalanceModel updatedBalance = context.balanceRepository().updateBalance(transaction.getAccountId(),
						transaction.getTransactionAmount());

				return new Balance(updatedBalance.getAccountId(),
						LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedBalance.getBalanceDate()),
								TimeZone.getDefault().toZoneId()),
						updatedBalance.getAvailableAmount());
			} catch (Exception e) {
				LOGGER.error("Register Transaction failed", e);
				throw new RuntimeException(e);
			}
		});
	}
}
