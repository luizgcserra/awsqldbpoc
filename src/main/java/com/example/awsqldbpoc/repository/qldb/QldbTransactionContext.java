package com.example.awsqldbpoc.repository.qldb;

import software.amazon.qldb.TransactionExecutor;

public class QldbTransactionContext {

	private static final ThreadLocal<TransactionExecutor> TRANSACTION_EXECUTOR = new ThreadLocal<>();

	public static void setTransactionExecutor(TransactionExecutor tx) {
		TRANSACTION_EXECUTOR.set(tx);
	}

	public static void clearTransactionExecutor() {
		TRANSACTION_EXECUTOR.remove();
	}

	public static TransactionExecutor getTransactionExecutor() {
		return TRANSACTION_EXECUTOR.get();
	}
}
