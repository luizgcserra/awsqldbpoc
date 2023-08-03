package com.example.awsqldbpoc.repository.sqlserver;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;

import io.micrometer.core.annotation.Timed;

@Profile("sqlserver")
@Repository
public class TransactionRepository implements ITransactionRepository {

	private static final String TABLE_NAME = "[Account].[Transaction]";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";
	private static final String TRANSACTION_DATE_FIELD_NAME = "transactionDate";
	private static final String TRANSACTION_ID_FIELD_NAME = "transactionId";
	private static final String TRANSACTION_AMOUNT_FIELD_NAME = "transactionAmount";
	private static final String DESCRIPTION_FIELD_NAME = "description";
	private static final String INSERT_QUERY = String.format(
			"INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", TABLE_NAME, ACCOUNT_ID_FIELD_NAME,
			TRANSACTION_ID_FIELD_NAME, TRANSACTION_DATE_FIELD_NAME, TRANSACTION_AMOUNT_FIELD_NAME,
			DESCRIPTION_FIELD_NAME);

	@Override
	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final TransactionModel transaction) throws Throwable {
		try (PreparedStatement statement = SqlServerSessionContext.get().prepareStatement(INSERT_QUERY)) {

			statement.setString(1, transaction.getAccountId());
			statement.setString(2, transaction.getTransactionId());
			statement.setLong(3, transaction.getTransactionDate());
			statement.setBigDecimal(4, transaction.getTransactionAmount());
			statement.setString(5, transaction.getDescription());

			statement.execute();
		}

		return true;
	}

	@Override
	@Timed(value = "registerTransactionBlock", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(List<TransactionModel> transactions) throws Throwable {

		try (PreparedStatement statement = SqlServerSessionContext.get().prepareStatement(INSERT_QUERY)) {

			for (TransactionModel transaction : transactions) {
				statement.setString(1, transaction.getAccountId());
				statement.setString(2, transaction.getTransactionId());
				statement.setLong(3, transaction.getTransactionDate());
				statement.setBigDecimal(4, transaction.getTransactionAmount());
				statement.setString(5, transaction.getDescription());
				statement.addBatch();
			}

			statement.executeBatch();
		}

		return true;
	}

}
