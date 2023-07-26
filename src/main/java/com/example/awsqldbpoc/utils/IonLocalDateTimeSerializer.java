package com.example.awsqldbpoc.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.amazon.ion.IonValue;
import com.amazon.ion.Timestamp;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.dataformat.ion.IonGenerator;

/**
 * Serializes [java.time.LocalDateTime] to Ion.
 */
public class IonLocalDateTimeSerializer extends StdScalarSerializer<LocalDateTime> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2245491338711047337L;

	public IonLocalDateTimeSerializer() {
		super(LocalDateTime.class);
	}

	@Override
	public void serialize(LocalDateTime datetime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		Timestamp timestamp = Timestamp.forMillis(datetime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
				null);
		((IonGenerator) jsonGenerator).writeValue(timestamp);
	}

	public static IonValue localDatetimeToTimestamp(LocalDateTime datetime) {
		return Constants.SYSTEM.newTimestamp(
				Timestamp.forMillis(datetime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), null));
	}
}