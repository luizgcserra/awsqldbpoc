package com.example.awsqldbpoc.repository.immudbkv;

import java.util.function.Function;

import org.apache.commons.pool2.ObjectPool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.UnitOfWork;
import com.example.awsqldbpoc.repository.immudb.ImmudDbSessionContext;

import io.codenotary.immudb4j.ImmuClient;

@Profile("immudbkv")
@Service
public class ImmuDbUnitOfWork implements UnitOfWork {

	private final ObjectPool<ImmuClient> immuClientPool;
	private final IBalanceRepository balanceRepository;
	private final ITransactionRepository transactionRepository;

	public ImmuDbUnitOfWork(ObjectPool<ImmuClient> immuClientPool, IBalanceRepository balanceRepository,
			ITransactionRepository transactionRepository) {
		super();
		this.immuClientPool = immuClientPool;
		this.balanceRepository = balanceRepository;
		this.transactionRepository = transactionRepository;
	}

	@Override
	public IBalanceRepository balanceRepository() {
		return balanceRepository;
	}

	@Override
	public ITransactionRepository transactionRepository() {
		return transactionRepository;
	}

	@Override
	public Balance execute(Function<UnitOfWork, Balance> action) {
		ImmuClient client = null;

		try {
			client = immuClientPool.borrowObject();
			//client.beginTransaction();

			ImmudDbSessionContext.set(client);

			Balance balance = action.apply(this);

			//client.commitTransaction();

			return balance;
		} catch (Exception e) {
			try {
				//client.rollbackTransaction();
			} catch (Exception e1) {
				try {
					immuClientPool.invalidateObject(client);
				} catch (Exception e2) {
				}
			}

			throw new RuntimeException(e);
		} finally {
			ImmudDbSessionContext.clear();

			if (null != client)
				try {
					immuClientPool.returnObject(client);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

}
