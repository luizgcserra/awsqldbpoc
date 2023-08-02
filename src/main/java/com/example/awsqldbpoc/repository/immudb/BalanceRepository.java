package com.example.awsqldbpoc.repository.immudb;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.codenotary.immudb4j.sql.SQLException;
import io.codenotary.immudb4j.sql.SQLQueryResult;
import io.codenotary.immudb4j.sql.SQLValue;
import io.micrometer.core.annotation.Timed;

@Profile("immudb")
@Repository
public class BalanceRepository implements IBalanceRepository {

	private static final String TABLE_NAME = "balances";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";
	private static final String SELECT_QUERY = "SELECT " + ACCOUNT_ID_FIELD_NAME
			+ ", balanceDate, availableAmount FROM " + TABLE_NAME + " WHERE " + ACCOUNT_ID_FIELD_NAME + " = ?";
	public static final String UPDATE_QUERY = "UPSERT INTO " + TABLE_NAME + " (" + ACCOUNT_ID_FIELD_NAME
			+ ", balanceDate, availableAmount) VALUES (?, ?, ?)";

	@Override
	@Timed(value = "updateBalance", percentiles = { .5, .9, .95, .99 })
	public BalanceModel updateBalance(String accountId, BigDecimal amount) throws Throwable {
		final BalanceModel currentBalance = this.getCurrentBalance(accountId);
		BalanceModel newAmount = null;

		if (currentBalance != null) {
			BigDecimal availableAmount = currentBalance.getAvailableAmount();
			newAmount = new BalanceModel(accountId, DateTimeUtils.toEpochMillis(LocalDateTime.now()),
					availableAmount.add(amount));
		} else {
			newAmount = new BalanceModel(accountId, DateTimeUtils.toEpochMillis(LocalDateTime.now()), amount);
		}

		ImmudDbSessionContext.get().sqlExec(UPDATE_QUERY, new SQLValue(accountId),
				new SQLValue(newAmount.getBalanceDate()), new SQLValue(newAmount.getAvailableAmount().toString()));

		return newAmount;
	}

	private BalanceModel getCurrentBalance(final String accountId) throws NumberFormatException, SQLException {

		SQLQueryResult result = ImmudDbSessionContext.get().sqlQuery(SELECT_QUERY, new SQLValue(accountId));

		if (result.next()) {
			return new BalanceModel(result.getString(0), result.getLong(1), Double.parseDouble(result.getString(2)));
		} else
			return null;

	}

}
