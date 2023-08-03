package com.example.awsqldbpoc.utils.sqlserver;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.example.awsqldbpoc.beans.SqlClientProperties;

public class ClientObjectFactory extends BasePooledObjectFactory<Connection> {

	private final SqlClientProperties properties;

	public ClientObjectFactory(SqlClientProperties properties) {
		this.properties = properties;
	}

	@Override
	public Connection create() throws Exception {

		String connectionUrl = String.format(
				"jdbc:sqlserver://%s:%s;database=%s;user=%s;password=%s;encrypt=false;trustServerCertificate=false;loginTimeout=10;",
				properties.getHost(), properties.getPort(), properties.getDatabase(), properties.getUsername(),
				properties.getPassword());

		return DriverManager.getConnection(connectionUrl);
	}

	@Override
	public PooledObject<Connection> wrap(Connection obj) {
		return new DefaultPooledObject<Connection>(obj);
	}

	@Override
	public void destroyObject(final PooledObject<Connection> p) throws Exception {
		try {
			p.getObject().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean validateObject(final PooledObject<Connection> p) {
		try {
			return p.getObject().createStatement().execute("SELECT 1");
		} catch (Exception e) {
			return false;
		}
	}
}
