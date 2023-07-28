package com.example.awsqldbpoc.repository.qldb;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazon.ion.IonValue;
import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.qldb.utils.Constants;
import com.example.awsqldbpoc.qldb.utils.IonLocalDateTimeSerializer;
import com.example.awsqldbpoc.repository.IBalanceRepository;

import io.micrometer.core.annotation.Timed;
import software.amazon.qldb.Result;

@Profile("qldb")
@Repository
public class BalanceRepository extends QldbRepository implements IBalanceRepository {

	private static final String UPDATE_QUERY = String.format(
			"UPDATE %s AS b SET b.BalanceDate = ?, b.AvailableAmount = ? WHERE b.AccountId = ?",
			Constants.BALANCE_TABLE_NAME);

	private static final String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE AccountId = ?",
			Constants.AVAILABLE_AMOUNT_FIELD_NAME, Constants.BALANCE_TABLE_NAME);

	private static final String INSERT_QUERY = String.format("INSERT INTO %s ?", Constants.BALANCE_TABLE_NAME);

	@Override
	@Timed(value = "updateBalance", percentiles = { .5, .9, .95, .99 })
	public BalanceModel updateBalance(final String accountId, final BigDecimal amount) throws IOException {

		final BalanceModel currentBalance = this.getCurrentBalance(accountId);
		BalanceModel newAmount = null;

		if (currentBalance != null) {
			BigDecimal availableAmount = currentBalance.getAvailableAmount();
			newAmount = new BalanceModel(accountId, LocalDateTime.now(), availableAmount.add(amount));

			final List<IonValue> parameters = new ArrayList<>();
			parameters.add(IonLocalDateTimeSerializer.localDatetimeToTimestamp(newAmount.getBalanceDate()));
			parameters.add(Constants.MAPPER.writeValueAsIonValue(newAmount.getAvailableAmount()));
			parameters.add(Constants.MAPPER.writeValueAsIonValue(newAmount.getAccountId()));

			execute(UPDATE_QUERY, parameters);

			return newAmount;
		} else {
			newAmount = new BalanceModel(accountId, LocalDateTime.now(), amount);

			execute(INSERT_QUERY, Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(newAmount)));

			return newAmount;
		}

	}

	private BalanceModel getCurrentBalance(final String accountId) throws IOException {

		final Result result = execute(SELECT_QUERY, Constants.MAPPER.writeValueAsIonValue(accountId));

		if (result.isEmpty())
			return null;
		else {
			return Constants.MAPPER.readValue(result.iterator().next(), BalanceModel.class);
		}
	}
}
