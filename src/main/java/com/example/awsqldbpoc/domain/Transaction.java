package com.example.awsqldbpoc.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

	private final String accountId;
	private final String transactionId;
	private final String uniqueId;
	private final LocalDateTime transactionDate;
	private final String description;
	private final BigDecimal transactionAmount;

	public Transaction(String accountId, String transactionId, LocalDateTime transactionDate, String description,
			BigDecimal transactionAmount) {
		super();
		this.accountId = accountId;
		this.transactionId = transactionId;
		this.uniqueId = accountId + "#" + transactionId;
		this.transactionDate = transactionDate;
		this.description = description;
		this.transactionAmount = transactionAmount;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

}
