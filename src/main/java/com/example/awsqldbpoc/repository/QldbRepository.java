package com.example.awsqldbpoc.repository;

import java.util.List;

import com.amazon.ion.IonValue;

import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public abstract class QldbRepository {

	private TransactionExecutor getTransactionExecutor() {
		return QldbTransactionContext.getTransactionExecutor();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Result execute(String statement, List parameters) {
		return this.getTransactionExecutor().execute(statement, parameters);
	}

	protected Result execute(String statement, IonValue... parameters) {
		return this.getTransactionExecutor().execute(statement, parameters);
	}
}
