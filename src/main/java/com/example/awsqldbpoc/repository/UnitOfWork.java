package com.example.awsqldbpoc.repository;

import java.util.function.Function;

import com.example.awsqldbpoc.domain.Balance;

public interface UnitOfWork {

	IBalanceRepository balanceRepository();

	ITransactionRepository transactionRepository();

	Balance execute(Function<UnitOfWork, Balance> action);
	
	String getRepositoryType();

}
