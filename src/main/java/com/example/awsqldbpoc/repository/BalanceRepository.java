package com.example.awsqldbpoc.repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.amazon.ion.IonValue;
import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.utils.Constants;
import com.example.awsqldbpoc.utils.IonLocalDateTimeSerializer;

import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public class BalanceRepository {

	private static final String UPDATE_QUERY = "UPDATE " + Constants.BALANCE_TABLE_NAME
			+ " AS b SET b.BalanceDate = ?, b.AvailableAmount = ? WHERE b.AccountId = ?";

	private static final String SELECT_QUERY = "SELECT AccountId, BalanceDate, AvailableAmount FROM "
			+ Constants.BALANCE_TABLE_NAME + " WHERE AccountId = ?";

	private static final String INSERT_QUERY = "INSERT INTO " + Constants.BALANCE_TABLE_NAME + " ?";

	private final TransactionExecutor txn;

	public BalanceRepository(TransactionExecutor txn) {
		super();
		this.txn = txn;
	}

	public Result updateBalance(final String accountId, final BigDecimal amount) throws IOException {

		final BalanceModel currentBalance = this.getCurrentBalance(accountId);

		if (currentBalance != null) {
			BigDecimal availableAmount = currentBalance.getAvailableAmount();
			BalanceModel newAmount = new BalanceModel(accountId, LocalDateTime.now(), availableAmount.add(amount));

			final List<IonValue> parameters = new ArrayList<>();
			parameters.add(IonLocalDateTimeSerializer.localDatetimeToTimestamp(newAmount.getBalanceDate()));
			parameters.add(Constants.MAPPER.writeValueAsIonValue(newAmount.getAvailableAmount()));
			parameters.add(Constants.MAPPER.writeValueAsIonValue(newAmount.getAccountId()));

			return txn.execute(UPDATE_QUERY, parameters);
		} else {
			final List<IonValue> parameters = Collections.singletonList(
					Constants.MAPPER.writeValueAsIonValue(new BalanceModel(accountId, LocalDateTime.now(), amount)));

			return txn.execute(INSERT_QUERY, parameters);
		}

	}

	private BalanceModel getCurrentBalance(final String accountId) throws IOException {

		final Result result = txn.execute(SELECT_QUERY, Constants.MAPPER.writeValueAsIonValue(accountId));

		if (result.isEmpty())
			return null;
		else
			return Constants.MAPPER.readValue(result.iterator().next(), BalanceModel.class);
	}
}
