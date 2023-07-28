package com.example.awsqldbpoc.repository;

import java.io.IOException;

import com.example.awsqldbpoc.models.TransactionModel;

public interface ITransactionRepository {

	boolean registerTransaction(TransactionModel transaction) throws IOException;

}
