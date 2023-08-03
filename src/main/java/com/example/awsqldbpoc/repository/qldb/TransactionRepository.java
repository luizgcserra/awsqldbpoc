package com.example.awsqldbpoc.repository.qldb;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazon.ion.IonValue;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.utils.qldb.Constants;

import io.micrometer.core.annotation.Timed;
import software.amazon.qldb.Result;

@Profile("qldb")
@Repository
public class TransactionRepository extends QldbRepository implements ITransactionRepository {

	private static final String INSERT_QUERY = String.format("INSERT INTO %s ? ", Constants.TRANSACTION_TABLE_NAME);
	private static final String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE %s = ?",
			Constants.TRANSACTION_UNIQUEID_FIELD_NAME, Constants.TRANSACTION_TABLE_NAME,
			Constants.TRANSACTION_UNIQUEID_FIELD_NAME);

	@Override
	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final TransactionModel transaction) throws IOException {

		if (!exists(transaction.getUniqueId())) {
			execute(INSERT_QUERY, Collections.singletonList(serialize(transaction)));

			return true;
		} else
			return false;
	}

	private IonValue serialize(Object obj) {
		try {
			return Constants.MAPPER.writeValueAsIonValue(obj);
		} catch (IOException e) {
			throw new RuntimeException("Ion Serialization error", e);
		}
	}

	private boolean exists(final String transactionUniqueId) throws IOException {

		final Result result = execute(SELECT_QUERY, Constants.SYSTEM.newString(transactionUniqueId));

		return !result.isEmpty();
	}

	@Override
	@Timed(value = "registerTransactionBlock", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final List<TransactionModel> transactions) throws IOException {

		for (TransactionModel tx : transactions)
			execute(INSERT_QUERY, Collections.singletonList(serialize(tx)));

		return true;
	}

}
