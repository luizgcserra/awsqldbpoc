package com.example.awsqldbpoc.repository.qldb;

import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.UnitOfWork;

import software.amazon.qldb.QldbDriver;

@Profile("qldb")
@Service
public class QldbUnitOfWork implements UnitOfWork {

	private final QldbDriver amazonQldbDriver;
	private final IBalanceRepository balanceRepository;
	private final ITransactionRepository transactionRepository;

	public QldbUnitOfWork(QldbDriver amazonQldbDriver, IBalanceRepository balanceRepository,
			ITransactionRepository transactionRepository) {
		super();
		this.amazonQldbDriver = amazonQldbDriver;
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
		return amazonQldbDriver.execute(txn -> {
			try {
				QldbTransactionContext.setTransactionExecutor(txn);

				return action.apply(this);
			} finally {
				QldbTransactionContext.clearTransactionExecutor();
			}
		});
	}

}
