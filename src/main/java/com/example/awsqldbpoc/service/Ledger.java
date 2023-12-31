package com.example.awsqldbpoc.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.domain.TransactionBlock;
import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.UnitOfWork;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.micrometer.core.annotation.Timed;

@Service
public class Ledger {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ledger.class);

	private final UnitOfWork unitOfWork;

	public Ledger(UnitOfWork unitOfWork) {
		super();
		this.unitOfWork = unitOfWork;
	}

	@Timed(value = "register", percentiles = { .5, .9, .95, .99 })
	public Balance register(Transaction transaction) {
		return this.unitOfWork.execute(context -> {
			try {
				if (context.transactionRepository()
						.registerTransaction(new TransactionModel(transaction.getAccountId(),
								transaction.getTransactionId(), transaction.getUniqueId(),
								DateTimeUtils.toEpochMillis(transaction.getTransactionDate()),
								transaction.getDescription(), transaction.getTransactionAmount()))) {

					BalanceModel updatedBalance = context.balanceRepository().updateBalance(transaction.getAccountId(),
							transaction.getTransactionAmount());

					return new Balance(updatedBalance.getAccountId(),
							DateTimeUtils.toLocalDateTime(updatedBalance.getBalanceDate()),
							updatedBalance.getAvailableAmount());
				} else
					return null;
			} catch (Throwable e) {
				LOGGER.error("Register Transaction failed", e);
				throw new RuntimeException(e);
			}
		});
	}

	@Timed(value = "registerBlock", percentiles = { .5, .9, .95, .99 })
	public Balance register(TransactionBlock transactionBlock) {
		return this.unitOfWork.execute(context -> {
			try {
				context.transactionRepository()
						.registerTransaction(transactionBlock.getAccountTransactions().stream()
								.map(transaction -> new TransactionModel(transaction.getAccountId(),
										transaction.getTransactionId(), transaction.getUniqueId(),
										DateTimeUtils.toEpochMillis(transaction.getTransactionDate()),
										transaction.getDescription(), transaction.getTransactionAmount()))
								.collect(Collectors.toList()));

				BalanceModel updatedBalance = context.balanceRepository().updateBalance(transactionBlock.getAccountId(),
						transactionBlock.getTotalAmount());

				return new Balance(updatedBalance.getAccountId(),
						DateTimeUtils.toLocalDateTime(updatedBalance.getBalanceDate()),
						updatedBalance.getAvailableAmount());

			} catch (Throwable e) {
				LOGGER.error("Register Transaction failed", e);
				throw new RuntimeException(e);
			}
		});
	}
}
