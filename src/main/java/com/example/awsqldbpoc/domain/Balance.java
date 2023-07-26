package com.example.awsqldbpoc.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Balance {

	private final String accountId;
	private final LocalDateTime balanceDate;
	private final BigDecimal availableAmount;

	public Balance(String accountId, LocalDateTime balanceDate, BigDecimal availableAmount) {
		super();
		this.accountId = accountId;
		this.balanceDate = balanceDate;
		this.availableAmount = availableAmount;
	}

	public String getAccountId() {
		return accountId;
	}

	public LocalDateTime getBalanceDate() {
		return balanceDate;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

}
