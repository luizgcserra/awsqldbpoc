package com.example.awsqldbpoc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.awsqldbpoc.utils.qldb.IonLocalDateTimeDeserializer;
import com.example.awsqldbpoc.utils.qldb.IonLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class BalanceModel {

	private final String accountId;
	private final LocalDateTime balanceDate;
	private final BigDecimal availableAmount;

	@JsonCreator
	public BalanceModel(@JsonProperty("AccountId") String accountId,
			@JsonProperty("BalanceDate") LocalDateTime balanceDate,
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
	@JsonSerialize(using = IonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = IonLocalDateTimeDeserializer.class)
	public LocalDateTime getBalanceDate() {
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
