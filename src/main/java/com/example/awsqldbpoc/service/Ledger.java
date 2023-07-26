package com.example.awsqldbpoc.service;

import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ContextRepository;

@Service
public class Ledger {

	private final ContextRepository contextRepository;

	public Ledger(ContextRepository contextRepository) {
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
}
