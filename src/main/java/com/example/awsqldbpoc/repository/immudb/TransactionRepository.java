package com.example.awsqldbpoc.repository.immudb;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;

import io.codenotary.immudb4j.sql.SQLValue;
import io.micrometer.core.annotation.Timed;

@Profile("immudb")
@Repository
public class TransactionRepository implements ITransactionRepository {

	private static final String TABLE_NAME = "transactions";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";
	private static final String TRANSACTION_DATE_FIELD_NAME = "transactionDate";
	private static final String TRANSACTION_ID_FIELD_NAME = "transactionId";
	private static final String TRANSACTION_AMOUNT_FIELD_NAME = "transactionAmount";
	private static final String DESCRIPTION_FIELD_NAME = "description";
	private static final String UPSERT_QUERY = String.format(
			"UPSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", TABLE_NAME, ACCOUNT_ID_FIELD_NAME,
			TRANSACTION_ID_FIELD_NAME, TRANSACTION_DATE_FIELD_NAME, TRANSACTION_AMOUNT_FIELD_NAME,
			DESCRIPTION_FIELD_NAME);

	@Override
	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final TransactionModel transaction) throws Throwable {
		ImmudDbSessionContext.get().sqlExec(UPSERT_QUERY, new SQLValue(transaction.getAccountId()),
				new SQLValue(transaction.getTransactionId()), new SQLValue(transaction.getTransactionDate()),
				new SQLValue(transaction.getTransactionAmount().toString()),
				new SQLValue(transaction.getDescription()));

		return true;
	}

}
