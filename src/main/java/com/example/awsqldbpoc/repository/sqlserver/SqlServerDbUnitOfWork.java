package com.example.awsqldbpoc.repository.sqlserver;

import java.sql.Connection;
import java.util.function.Function;

import org.apache.commons.pool2.ObjectPool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.UnitOfWork;

@Profile("sqlserver")
@Service
public class SqlServerDbUnitOfWork implements UnitOfWork {

	private final ObjectPool<Connection> sqlClientPool;
	private final IBalanceRepository balanceRepository;
	private final ITransactionRepository transactionRepository;

	public SqlServerDbUnitOfWork(ObjectPool<Connection> sqlClientPool, IBalanceRepository balanceRepository,
			ITransactionRepository transactionRepository) {
		super();
		this.sqlClientPool = sqlClientPool;
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
		Connection client = null;

		try {
			client = sqlClientPool.borrowObject();
			client.setAutoCommit(false);

			SqlServerSessionContext.set(client);

			Balance balance = action.apply(this);

			client.commit();

			return balance;
		} catch (Exception e) {
			try {
				client.rollback();
			} catch (Exception e1) {
				try {
					sqlClientPool.invalidateObject(client);
				} catch (Exception e2) {
				}
			}

			throw new RuntimeException(e);
		} finally {
			SqlServerSessionContext.clear();

			if (null != client)
				try {
					sqlClientPool.returnObject(client);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

}
