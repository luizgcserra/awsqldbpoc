package com.example.awsqldbpoc.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {

	private static final ZoneId ZONE_ID = ZoneId.systemDefault();

	public static long toEpochMillis(LocalDateTime datetime) {
		return ZonedDateTime.of(datetime, ZONE_ID).toInstant().toEpochMilli();
	}

	public static LocalDateTime toLocalDateTime(long epochMillis) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZONE_ID);
	}

}
