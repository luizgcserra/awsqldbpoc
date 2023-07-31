package com.example.awsqldbpoc.repository.dynamo;

import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.awsqldbpoc.domain.Balance;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.repository.UnitOfWork;

@Profile("dynamo")
@Service
public class DynamoUnitOfWork implements UnitOfWork {

	private final IBalanceRepository balanceRepository;
	private final ITransactionRepository transactionRepository;

	public DynamoUnitOfWork(IBalanceRepository balanceRepository, ITransactionRepository transactionRepository) {
		super();
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
		return action.apply(this);
	}

}
