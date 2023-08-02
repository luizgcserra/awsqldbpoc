package com.example.awsqldbpoc.repository.immudbkv;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;

import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.immudb.ImmudDbSessionContext;

import io.micrometer.core.annotation.Timed;

@Profile("immudbkv")
@Repository
public class TransactionRepository implements ITransactionRepository {

	private static final String TABLE_NAME = "transactions";

	@Override
	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final TransactionModel transaction) throws Throwable {
		ImmudDbSessionContext.get().set(TABLE_NAME + "#" + transaction.getUniqueId(),
				SerializationUtils.serialize(transaction));

		return true;
	}

}
