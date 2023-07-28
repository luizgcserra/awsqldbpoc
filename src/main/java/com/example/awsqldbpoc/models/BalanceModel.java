package com.example.awsqldbpoc.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class BalanceModel {

	private final String accountId;
	private final Long balanceDate;
	private final BigDecimal availableAmount;

	@JsonCreator
	public BalanceModel(@JsonProperty("AccountId") String accountId,
			@JsonProperty("BalanceDate") Long balanceDate,
			@JsonProperty("AvailableAmount") BigDecimal availableAmount) {
		super();
		this.accountId = accountId;
		this.balanceDate = balanceDate;
		this.availableAmount = availableAmount;
	}

	@JsonProperty("AccountId")
	public String getAccountId() {
		return accountId;
	}

	@JsonProperty("BalanceDate")
	public Long getBalanceDate() {
		return balanceDate;
	}

	@JsonProperty("AvailableAmount")
	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	@Override
	public String toString() {
		return "Amount{accountId='" + accountId + "', balanceDate=" + balanceDate + ", availableAmount="
				+ availableAmount + "}";
	}

}
