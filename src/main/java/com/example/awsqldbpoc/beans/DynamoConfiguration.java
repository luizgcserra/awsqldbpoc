package com.example.awsqldbpoc.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

@Profile("dynamo")
@Configuration
public class DynamoConfiguration {

	@Value("${cloud.aws.region}")
	private String region;

	@Bean(destroyMethod = "shutdown")
	public AmazonDynamoDB amazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard().withRegion(this.region).build();
	}

	@Bean(destroyMethod = "shutdown")
	public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
		return new DynamoDB(amazonDynamoDB);
	}

}
