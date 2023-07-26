package com.example.awsqldbpoc.repository;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.TransactionExecutor;

@Service
public class ContextRepository {

	private static final ThreadLocal<TransactionExecutor> TRANSACTION_EXECUTOR = new ThreadLocal<>();

	private final QldbDriver amazonQldbDriver;

	private BalanceRepository balanceRepository;
	private TransactionRepository transactionRepository;

	public ContextRepository(QldbDriver amazonQldbDriver) {
		super();
		this.amazonQldbDriver = amazonQldbDriver;
	}

	public BalanceRepository balanceRepository() {
		return balanceRepository;
	}

	public TransactionRepository transactionRepository() {
		return transactionRepository;
	}

	public TransactionExecutor getTransactionExecutor() {
		return TRANSACTION_EXECUTOR.get();
	}

	public void execute(Consumer<ContextRepository> action) {

		amazonQldbDriver.execute(txn -> {
			TRANSACTION_EXECUTOR.set(txn);

			balanceRepository = new BalanceRepository(txn);
			transactionRepository = new TransactionRepository(txn);

			action.accept(this);
		});
	}

}
