package com.example.awsqldbpoc.repository.qldb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ion.IonValue;

import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public abstract class QldbRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(QldbRepository.class);

	private TransactionExecutor getTransactionExecutor() {
		return QldbTransactionContext.getTransactionExecutor();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Result execute(String statement, List parameters) {
		Result result = this.getTransactionExecutor().execute(statement, parameters);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Query '{}' executed in {} ms. Read I/O: {} -  ", statement,
					result.getTimingInformation().getProcessingTimeMilliseconds(),
					result.getConsumedIOs().getReadIOs());
		}

		return result;
	}

	protected Result execute(String statement, IonValue... parameters) {
		Result result = this.getTransactionExecutor().execute(statement, parameters);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Query '{}' executed in {} ms. Read I/O: {} -  ", statement,
					result.getTimingInformation().getProcessingTimeMilliseconds(),
					result.getConsumedIOs().getReadIOs());
		}

		return result;
	}
}
