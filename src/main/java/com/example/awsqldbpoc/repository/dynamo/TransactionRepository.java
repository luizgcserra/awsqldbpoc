package com.example.awsqldbpoc.repository.dynamo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.example.awsqldbpoc.models.TransactionModel;
import com.example.awsqldbpoc.repository.ITransactionRepository;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.micrometer.core.annotation.Timed;

@Profile("dynamo")
@Repository
public class TransactionRepository implements ITransactionRepository {

	private static final String UNIQUE_ID_FIELD_NAME = "uniqueId";
	private static final String TABLE_NAME = "transactions";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";
	private static final String TRANSACTION_DATE_FIELD_NAME = "transactionDate";
	private static final String TRANSACTION_ID_FIELD_NAME = "transactionId";
	private static final String TRANSACTION_AMOUNT_FIELD_NAME = "transactionAmount";
	private static final String DESCRIPTION_FIELD_NAME = "description";
	private final AmazonDynamoDB dynamoDB;

	@Value("${dynamo.idempotent:false}")
	private boolean idempotent;

	public TransactionRepository(AmazonDynamoDB dynamoDB) {
		this.dynamoDB = dynamoDB;
	}

	@Override
	@Timed(value = "registerTransaction", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(final TransactionModel transaction) throws IOException {

		if (!exists(transaction.getUniqueId())) {
			Map<String, AttributeValue> attributesMap = new HashMap<>();

			attributesMap.put(UNIQUE_ID_FIELD_NAME, new AttributeValue(transaction.getUniqueId()));
			attributesMap.put(TRANSACTION_DATE_FIELD_NAME,
					new AttributeValue().withN(String.valueOf(DateTimeUtils.toEpochMillis(LocalDateTime.now()))));
			attributesMap.put(ACCOUNT_ID_FIELD_NAME, new AttributeValue(transaction.getAccountId()));
			attributesMap.put(TRANSACTION_ID_FIELD_NAME, new AttributeValue(transaction.getTransactionId()));
			attributesMap.put(TRANSACTION_AMOUNT_FIELD_NAME,
					new AttributeValue().withN(transaction.getTransactionAmount().toString()));
			attributesMap.put(DESCRIPTION_FIELD_NAME, new AttributeValue(transaction.getDescription()));

			dynamoDB.putItem(new PutItemRequest().withTableName(TABLE_NAME).withItem(attributesMap));

			return true;
		} else
			return false;
	}

	private boolean exists(final String transactionUniqueId) throws IOException {
		if (idempotent) {
			Map<String, AttributeValue> attributesMap = Collections.singletonMap(UNIQUE_ID_FIELD_NAME,
					new AttributeValue(transactionUniqueId));

			GetItemResult result = dynamoDB.getItem(TABLE_NAME, attributesMap, true);

			return result.getItem() != null;
		} else
			return false;
	}

	@Override
	@Timed(value = "registerTransactionBlock", percentiles = { .5, .9, .95, .99 })
	public boolean registerTransaction(List<TransactionModel> transactions) throws Throwable {
		List<WriteRequest> items = new ArrayList<>();
		Map<String, AttributeValue> attributesMap = null;

		for (TransactionModel transaction : transactions) {
			attributesMap = new HashMap<>();
			attributesMap.put(UNIQUE_ID_FIELD_NAME, new AttributeValue(transaction.getUniqueId()));
			attributesMap.put(TRANSACTION_DATE_FIELD_NAME,
					new AttributeValue().withN(String.valueOf(DateTimeUtils.toEpochMillis(LocalDateTime.now()))));
			attributesMap.put(ACCOUNT_ID_FIELD_NAME, new AttributeValue(transaction.getAccountId()));
			attributesMap.put(TRANSACTION_ID_FIELD_NAME, new AttributeValue(transaction.getTransactionId()));
			attributesMap.put(TRANSACTION_AMOUNT_FIELD_NAME,
					new AttributeValue().withN(transaction.getTransactionAmount().toString()));
			attributesMap.put(DESCRIPTION_FIELD_NAME, new AttributeValue(transaction.getDescription()));

			items.add(new WriteRequest(new PutRequest().withItem(attributesMap)));
		}

		BatchWriteItemRequest withRequestItems = new BatchWriteItemRequest()
				.withRequestItems(Collections.singletonMap(TABLE_NAME, items));

		dynamoDB.batchWriteItem(withRequestItems);

		return true;
	}
}
