package com.example.awsqldbpoc.beans;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awsqldbpoc.utils.qldb.Constants;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.awssdk.services.qldbsession.QldbSessionClientBuilder;
import software.amazon.qldb.BackoffStrategy;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.RetryPolicy;
import software.amazon.qldb.RetryPolicyContext;

@Qualifier("qldb")
@Configuration
public class QldbConfiguration {

	@Value("${cloud.aws.region}")
	private String region;

	@Value("${max-concurrent-transactions:1000}")
	private int maxConcurrentTransactions;

	@Bean(destroyMethod = "close")
	public QldbDriver amazonQldbDriver(QldbSessionClientBuilder sessionBuilder) {
		return QldbDriver.builder().ledger(Constants.LEDGER_NAME).transactionRetryPolicy(
				RetryPolicy.builder().maxRetries(Constants.RETRY_LIMIT).backoffStrategy(new BackoffStrategy() {

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
