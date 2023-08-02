package com.example.awsqldbpoc.utils.immudb;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.example.awsqldbpoc.beans.ClientProperties;

import io.codenotary.immudb4j.ImmuClient;

public class ClientObjectFactory extends BasePooledObjectFactory<ImmuClient> {

	private final ClientProperties properties;

	public ClientObjectFactory(ClientProperties properties) {
		this.properties = properties;
	}

	@Override
	public ImmuClient create() throws Exception {

		ImmuClient client = ImmuClient.newBuilder().withServerUrl(properties.getHost())
				.withServerPort(properties.getPort()).withKeepAlivePeriod(60 * 5).withChunkSize(1024 * 256).build();
		client.openSession(properties.getDatabase(), properties.getUsername(), properties.getPassword());

		return client;
	}

	@Override
	public PooledObject<ImmuClient> wrap(ImmuClient obj) {
		return new DefaultPooledObject<ImmuClient>(obj);
	}

	@Override
	public void destroyObject(final PooledObject<ImmuClient> p) throws Exception {
		try {
			p.getObject().closeSession();
			p.getObject().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean validateObject(final PooledObject<ImmuClient> p) {
		return p.getObject().healthCheck();
	}
}
