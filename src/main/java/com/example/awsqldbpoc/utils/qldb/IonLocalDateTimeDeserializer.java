package com.example.awsqldbpoc.utils.qldb;

import java.io.IOException;
import java.time.LocalDateTime;

import com.amazon.ion.Timestamp;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializes [java.time.LocalDateTime] from Ion.
 */
public class IonLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		return timestampToLocalDate((Timestamp) jp.getEmbeddedObject());
	}

	@SuppressWarnings("deprecation")
	private LocalDateTime timestampToLocalDate(Timestamp timestamp) {
		return LocalDateTime.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), timestamp.getHour(),
				timestamp.getMinute(), timestamp.getSecond(),
				(int) timestamp.getFractionalSecond().floatValue() * 1000);
	}
}