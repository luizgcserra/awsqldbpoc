package com.example.awsqldbpoc.qldb.utils;

import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper;
import com.fasterxml.jackson.dataformat.ion.ionvalue.IonValueMapper;

public final class Constants {
	public static final int RETRY_LIMIT = 4;
	public static final String LEDGER_NAME = "myledger";
	public static final String BALANCE_TABLE_NAME = "balances";
	public static final String TRANSACTION_TABLE_NAME = "transactions";
	public static final String USER_TABLES = "information_schema.user_tables";
	public static final String AVAILABLE_AMOUNT_FIELD_NAME = "AvailableAmount";
	public static final IonSystem SYSTEM = IonSystemBuilder.standard().build();
	public static final IonObjectMapper MAPPER = new IonValueMapper(SYSTEM);

	private Constants() {
	}

	static {
		MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	};

}