package com.example.awsqldbpoc.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.domain.TransactionBlock;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.QldbUnitOfWork;

@Service
public class Ledger {

	private final QldbUnitOfWork contextRepository;

	public Ledger(QldbUnitOfWork contextRepository) {
		super();
		this.contextRepository = contextRepository;
	}

	public void registerTransaction(Transaction transaction) {
		this.contextRepository.execute(context -> {
			try {
				context.transactionRepository().registerTransaction(new TransactionModel(transaction.getAccountId(),
						transaction.getTransactionId(), transaction.getUniqueId(), transaction.getTransactionDate(),
						transaction.getDescription(), transaction.getTransactionAmount()));
				context.balanceRepository().updateBalance(transaction.getAccountId(),
						transaction.getTransactionAmount());

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}

	public void registerTransaction(TransactionBlock transactionBlock) {
		this.contextRepository.execute(context -> {
			try {
				context.transactionRepository()
						.registerTransaction(transactionBlock.getAccountTransactions().stream()
								.map(transaction -> new TransactionModel(transaction.getAccountId(),
										transaction.getTransactionId(), transaction.getUniqueId(),
										transaction.getTransactionDate(), transaction.getDescription(),
										transaction.getTransactionAmount()))
								.collect(Collectors.toList()));

				context.balanceRepository().updateBalance(transactionBlock.getAccountId(),
						transactionBlock.getTotalAmount());

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}
}
