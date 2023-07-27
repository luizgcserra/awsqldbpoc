package com.example.awsqldbpoc.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.amazon.ion.IonValue;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.utils.Constants;

import software.amazon.qldb.Result;

@Repository
public class TransactionRepository extends QldbRepository {

	private static final String INSERT_QUERY = "INSERT INTO " + Constants.TRANSACTION_TABLE_NAME + " ?";

	public Result registerTransaction(final TransactionModel transaction) throws IOException {

		return execute(INSERT_QUERY, Collections.singletonList(serialize(transaction)));
	}

	public boolean registerTransaction(final List<TransactionModel> transactions) throws IOException {

		for (TransactionModel tx : transactions)
			execute(INSERT_QUERY, Collections.singletonList(serialize(tx)));

		return true;
	}

	private IonValue serialize(Object obj) {
		try {
			return Constants.MAPPER.writeValueAsIonValue(obj);
		} catch (IOException e) {
			throw new RuntimeException("Ion Serialization error", e);
		}
	}
}
