package com.example.awsqldbpoc.repository;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import software.amazon.qldb.QldbDriver;

@Service
public class QldbUnitOfWork {

	private final QldbDriver amazonQldbDriver;
	private final BalanceRepository balanceRepository;
	private final TransactionRepository transactionRepository;

	public QldbUnitOfWork(QldbDriver amazonQldbDriver, BalanceRepository balanceRepository,
			TransactionRepository transactionRepository) {
		super();
		this.amazonQldbDriver = amazonQldbDriver;
		this.balanceRepository = balanceRepository;
		this.transactionRepository = transactionRepository;
	}

	public BalanceRepository balanceRepository() {
		return balanceRepository;
	}

	public TransactionRepository transactionRepository() {
		return transactionRepository;
	}

	public void execute(Consumer<QldbUnitOfWork> action) {

		amazonQldbDriver.execute(txn -> {
			try {
				QldbTransactionContext.setTransactionExecutor(txn);

				action.accept(this);
			} finally {
				QldbTransactionContext.clearTransactionExecutor();
			}
		});
	}

}
