package com.example.awsqldbpoc.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sqlserver")
public class SqlClientProperties {

	private String host;
	private int port;
	private String database;
	private String username;
	private String password;
	private int maxSessions;

	public SqlClientProperties() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SqlClientProperties(String host, int port, String database, String username, String password,
			int maxSessions) {
		super();
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.maxSessions = maxSessions;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxSessions() {
		return maxSessions;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

}
