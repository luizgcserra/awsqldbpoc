package com.example.awsqldbpoc.repository;

import java.util.List;

import com.example.awsqldbpoc.models.TransactionModel;

public interface ITransactionRepository {

	boolean registerTransaction(TransactionModel transaction) throws Throwable;

	boolean registerTransaction(List<TransactionModel> transactions) throws Throwable;
}
