package com.example.awsqldbpoc.repository;

import java.io.IOException;
import java.math.BigDecimal;

import com.example.awsqldbpoc.models.BalanceModel;

public interface IBalanceRepository {

	BalanceModel updateBalance(String accountId, BigDecimal amount) throws IOException;

}
