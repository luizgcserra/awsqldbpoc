package com.example.awsqldbpoc.repository.immudbkv;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;

import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.repository.immudb.ImmudDbSessionContext;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.codenotary.immudb4j.Entry;
import io.codenotary.immudb4j.exceptions.KeyNotFoundException;
import io.codenotary.immudb4j.exceptions.VerificationException;
import io.codenotary.immudb4j.sql.SQLException;
import io.micrometer.core.annotation.Timed;

@Profile("immudbkv")
@Repository
public class BalanceRepository implements IBalanceRepository {

	private static final String TABLE_NAME = "balances";

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

		ImmudDbSessionContext.get().set(TABLE_NAME + "#" + accountId, SerializationUtils.serialize(newAmount));

		return newAmount;
	}

	private BalanceModel getCurrentBalance(final String accountId)
			throws NumberFormatException, SQLException, VerificationException {

		try {
			Entry entry = ImmudDbSessionContext.get().verifiedGet(TABLE_NAME + "#" + accountId);

			return (BalanceModel) SerializationUtils.deserialize(entry.getValue());
		} catch (KeyNotFoundException e) {
			return null;
		} catch (VerificationException e) {
			throw e;
		}
	}

}
