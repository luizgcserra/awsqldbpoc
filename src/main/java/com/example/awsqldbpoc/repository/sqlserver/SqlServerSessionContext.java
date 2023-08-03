package com.example.awsqldbpoc.repository.sqlserver;

import java.sql.Connection;

public class SqlServerSessionContext {

	private static final ThreadLocal<Connection> SQL_CLIENT = new ThreadLocal<>();

	public static void set(Connection client) {
		SQL_CLIENT.set(client);
	}

	public static void clear() {
		SQL_CLIENT.remove();
	}

	public static Connection get() {
		return SQL_CLIENT.get();
	}
}
