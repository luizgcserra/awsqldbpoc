package com.example.awsqldbpoc.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.amazon.ion.IonValue;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.utils.Constants;

import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public class TransactionRepository {

	private static final String INSERT_QUERY = "INSERT INTO " + Constants.TRANSACTION_TABLE_NAME + " ?";

	private final TransactionExecutor txn;

	public TransactionRepository(TransactionExecutor txn) {
		super();
		this.txn = txn;
	}

	public Result registerTransaction(final TransactionModel transaction) throws IOException {

		final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(transaction));

		return txn.execute(INSERT_QUERY, parameters);
	}
}
