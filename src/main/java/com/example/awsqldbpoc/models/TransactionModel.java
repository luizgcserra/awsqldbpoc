package com.example.awsqldbpoc.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.awsqldbpoc.utils.qldb.IonLocalDateTimeDeserializer;
import com.example.awsqldbpoc.utils.qldb.IonLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class TransactionModel {

	private final String accountId;
	private final String transactionId;
	private final String uniqueId;
	private final LocalDateTime transactionDate;
	private final String description;
	private final BigDecimal transactionAmount;

	@JsonCreator
	public TransactionModel(@JsonProperty("AccountId") String accountId, @JsonProperty("TransactionId") String transactionId,
			@JsonProperty("UniqueId") String uniqueId, @JsonProperty("TransactionDate") LocalDateTime transactionDate,
			@JsonProperty("Description") String description,
			@JsonProperty("TransactionAmount") BigDecimal transactionAmount) {
		super();
		this.accountId = accountId;
		this.transactionId = transactionId;
		this.uniqueId = uniqueId;
		this.transactionDate = transactionDate;
		this.description = description;
		this.transactionAmount = transactionAmount;
	}

	@JsonProperty("AccountId")
	public String getAccountId() {
		return accountId;
	}

	@JsonProperty("TransactionId")
	public String getTransactionId() {
		return transactionId;
	}

	@JsonProperty("UniqueId")
	public String getUniqueId() {
		return uniqueId;
	}

	@JsonProperty("TransactionDate")
	@JsonSerialize(using = IonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = IonLocalDateTimeDeserializer.class)
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("TransactionAmount")
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	@Override
	public String toString() {
		return "Transaction{accountId='" + accountId + "', transactionId='" + transactionId + "', uniqueId='"
				+ uniqueId + "', transactionDate=" + transactionDate + ", description='" + description
				+ "', transactionAmount=" + transactionAmount + "}";
	}

	
}
