package com.example.awsqldbpoc.repository.immudbkv;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;

import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.immudb.ImmudDbSessionContext;

import io.codenotary.immudb4j.KVListBuilder;
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

	@Override
	@Timed(value = "registerTransactionBlock", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(List<TransactionModel> transactions) throws Throwable {
		KVListBuilder kvListBuilder = KVListBuilder.newBuilder();

		for (int i = 0; i < transactions.size(); i++) {
			kvListBuilder.add(TABLE_NAME + "#" + transactions.get(i).getUniqueId(),
					SerializationUtils.serialize(transactions.get(i)));
		}

		ImmudDbSessionContext.get().setAll(kvListBuilder.entries());

		return true;
	}

}
