package com.example.awsqldbpoc.utils.qldb;

import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper;
import com.fasterxml.jackson.dataformat.ion.ionvalue.IonValueMapper;

public final class Constants {
	
	public static final IonSystem SYSTEM = IonSystemBuilder.standard().build();
	public static final IonObjectMapper MAPPER = new IonValueMapper(SYSTEM);

	private Constants() {
	}

	static {
		MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	};

}