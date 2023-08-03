package com.example.awsqldbpoc.repository.sqlserver;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.micrometer.core.annotation.Timed;

@Profile("sqlserver")
@Repository
public class BalanceRepository implements IBalanceRepository {

	private static final String TABLE_NAME = "[Account].[Balance]";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";
	private static final String SELECT_QUERY = "SELECT " + ACCOUNT_ID_FIELD_NAME
			+ ", balanceDate, availableAmount FROM " + TABLE_NAME + " WHERE " + ACCOUNT_ID_FIELD_NAME + " = ?";
	public static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME
			+ " SET balanceDate = ?, availableAmount = ? Where accountId = ?";
	public static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + "(" + ACCOUNT_ID_FIELD_NAME
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
			updateBalance(newAmount);
		} else {
			newAmount = new BalanceModel(accountId, DateTimeUtils.toEpochMillis(LocalDateTime.now()), amount);
			insertBalance(newAmount);
		}

		return newAmount;
	}

	private void insertBalance(BalanceModel model) throws SQLException {
		try (PreparedStatement statement = SqlServerSessionContext.get().prepareStatement(INSERT_QUERY)) {

			statement.setString(1, model.getAccountId());
			statement.setLong(2, model.getBalanceDate());
			statement.setBigDecimal(3, model.getAvailableAmount());

			statement.execute();
		}
	}

	private void updateBalance(BalanceModel model) throws SQLException {
		try (PreparedStatement statement = SqlServerSessionContext.get().prepareStatement(UPDATE_QUERY)) {

			statement.setLong(1, model.getBalanceDate());
			statement.setBigDecimal(2, model.getAvailableAmount());
			statement.setString(3, model.getAccountId());

			statement.execute();
		}
	}

	private BalanceModel getCurrentBalance(final String accountId) throws java.sql.SQLException {
		try (PreparedStatement statement = SqlServerSessionContext.get().prepareStatement(SELECT_QUERY)) {

			statement.setString(1, accountId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next())
					return new BalanceModel(resultSet.getString(1), resultSet.getLong(2), resultSet.getBigDecimal(3));
				else
					return null;
			}
		}
	}

}
