package com.example.awsqldbpoc.utils.qldb;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.dataformat.ion.IonGenerator;

/**
 * Serializes [java.time.LocalDate] to Ion.
 */
public class IonBigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1885028058330523348L;

	public IonBigDecimalSerializer() {
		super(BigDecimal.class);
	}

	@Override
	public void serialize(BigDecimal decimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		((IonGenerator) jsonGenerator).writeString(decimal.toString());
	}
}