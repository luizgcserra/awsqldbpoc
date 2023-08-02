package com.example.awsqldbpoc.repository.immudb;

import io.codenotary.immudb4j.ImmuClient;

public class ImmudDbSessionContext {

	private static final ThreadLocal<ImmuClient> IMMUDB_CLIENT = new ThreadLocal<>();

	public static void set(ImmuClient client) {
		IMMUDB_CLIENT.set(client);
	}

	public static void clear() {
		IMMUDB_CLIENT.remove();
	}

	public static ImmuClient get() {
		return IMMUDB_CLIENT.get();
	}
}
