package com.example.awsqldbpoc.repository.dynamo;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.example.awsqldbpoc.models.BalanceModel;
import com.example.awsqldbpoc.repository.IBalanceRepository;
import com.example.awsqldbpoc.utils.DateTimeUtils;

import io.micrometer.core.annotation.Timed;

@Profile("dynamo")
@Repository
public class BalanceRepository implements IBalanceRepository {

	private static final String TABLE_NAME = "balances";
	private static final String ACCOUNT_ID_FIELD_NAME = "accountId";

	private final AmazonDynamoDB dynamoDB;

	public BalanceRepository(AmazonDynamoDB dynamoDB) {
		this.dynamoDB = dynamoDB;

	}

	@Override
	@Timed(value = "updateBalance", percentiles = { .5, .9, .95, .99 })
	public BalanceModel updateBalance(String accountId, BigDecimal amount) throws IOException {
		final BalanceModel currentBalance = this.getCurrentBalance(accountId);
		BalanceModel newAmount = null;

		if (currentBalance != null) {
			BigDecimal availableAmount = currentBalance.getAvailableAmount();
			newAmount = new BalanceModel(accountId, DateTimeUtils.toEpochMillis(LocalDateTime.now()),
					availableAmount.add(amount));
		} else {
			newAmount = new BalanceModel(accountId, DateTimeUtils.toEpochMillis(LocalDateTime.now()), amount);
		}

		Map<String, AttributeValue> attributesMap = new HashMap<>();

		attributesMap.put(ACCOUNT_ID_FIELD_NAME, new AttributeValue(accountId));
		attributesMap.put("balanceDate",
				new AttributeValue().withN(String.valueOf(DateTimeUtils.toEpochMillis(LocalDateTime.now()))));
		attributesMap.put("availableAmount", new AttributeValue().withN(newAmount.getAvailableAmount().toString()));

		dynamoDB.putItem(new PutItemRequest().withTableName(TABLE_NAME).withItem(attributesMap));

		return newAmount;
	}

	private BalanceModel getCurrentBalance(final String accountId) throws IOException {

		Map<String, AttributeValue> attributesMap = Collections.singletonMap(ACCOUNT_ID_FIELD_NAME,
				new AttributeValue(accountId));

		GetItemResult result = dynamoDB.getItem(TABLE_NAME, attributesMap, true);

		if (result.getItem() == null)
			return null;
		else {
			return new BalanceModel(accountId, Long.parseLong(result.getItem().get("balanceDate").getN()),
					Double.parseDouble((result.getItem().get("availableAmount").getN())));
		}
	}

}
