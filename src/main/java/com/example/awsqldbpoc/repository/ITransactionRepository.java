package com.example.awsqldbpoc.repository;

import java.io.IOException;
import java.util.List;

import com.example.awsqldbpoc.models.TransactionModel;

public interface ITransactionRepository {

	boolean registerTransaction(TransactionModel transaction) throws IOException;

	boolean registerTransaction(List<TransactionModel> transactions) throws IOException;

}
