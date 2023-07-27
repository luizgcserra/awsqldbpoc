package com.example.awsqldbpoc.domain;

import java.math.BigDecimal;
import java.util.List;

public class TransactionBlock {

	private final String accountId;
	private final List<Transaction> accountTransactions;

	public TransactionBlock(String accountId, List<Transaction> accountTransactions) {
		super();
		this.accountId = accountId;
		this.accountTransactions = accountTransactions;
	}

	public String getAccountId() {
		return accountId;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal amount = BigDecimal.ZERO;

		for (int i = 0; i < this.accountTransactions.size(); i++) {
			amount.add(this.getAccountTransactions().get(i).getTransactionAmount());
		}

		return amount;
	}

	public List<Transaction> getAccountTransactions() {
		return accountTransactions;
	}

	public boolean isValid() {
		if (this.accountId == null || this.accountTransactions == null || this.accountTransactions.isEmpty())
			return false;

		for (int i = 0; i < this.accountTransactions.size(); i++) {
			if (!this.accountTransactions.get(i).getAccountId().equalsIgnoreCase(this.getAccountId()))
				return false;
		}

		return true;
	}
}
