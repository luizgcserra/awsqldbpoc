package com.example.awsqldbpoc.beans;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.awssdk.services.qldbsession.QldbSessionClientBuilder;
import software.amazon.qldb.BackoffStrategy;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.RetryPolicy;
import software.amazon.qldb.RetryPolicyContext;

@Profile("qldb")
@Configuration
public class QldbConfiguration {

	@Value("${cloud.aws.region}")
	private String region;

	@Value("${max-concurrent-transactions:1000}")
	private int maxConcurrentTransactions;

	@Value("${qldb.name}")
	private String ledgerName;

	@Value("${qldb.rety-limit:99}")
	private int retryLimit;

	@Bean(destroyMethod = "close")
	public QldbDriver amazonQldbDriver(QldbSessionClientBuilder sessionBuilder) {
		return QldbDriver.builder().ledger(ledgerName).transactionRetryPolicy(
				RetryPolicy.builder().maxRetries(retryLimit).backoffStrategy(new BackoffStrategy() {

					@Override
					public Duration calculateDelay(RetryPolicyContext arg0) {
						return Duration.ofMillis(10).multipliedBy(arg0.retriesAttempted());
					}
				}).build()).sessionClientBuilder(sessionBuilder).maxConcurrentTransactions(maxConcurrentTransactions)
				.build();
	}

	@Bean
	public QldbSessionClientBuilder amazonQldbSessionClientBuilder() {
		return QldbSessionClient.builder().region(Region.of(this.region));
	}
}
