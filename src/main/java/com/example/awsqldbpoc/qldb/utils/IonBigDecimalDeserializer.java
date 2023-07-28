package com.example.awsqldbpoc.qldb.utils;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializes [java.time.LocalDateTime] from Ion.
 */
public class IonBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		return decimalToBigDecimal((String) jp.getEmbeddedObject());
	}

	@SuppressWarnings("deprecation")
	private BigDecimal decimalToBigDecimal(String value) {
		return new BigDecimal(Double.parseDouble(value)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}