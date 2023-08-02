package com.example.awsqldbpoc.repository;

import com.example.awsqldbpoc.models.TransactionModel;

public interface ITransactionRepository {

	boolean registerTransaction(TransactionModel transaction) throws Throwable;

}
