package com.example.awsqldbpoc.beans;

import java.time.Duration;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.awsqldbpoc.utils.immudb.ClientObjectFactory;

import io.codenotary.immudb4j.ImmuClient;

@Profile("immudb")
@Configuration
public class ImmuDbConfiguration {
	
	@Bean(destroyMethod = "close")
	public ObjectPool<ImmuClient> immuClientPool(ClientProperties properties) throws Exception {
		GenericObjectPoolConfig<ImmuClient> config = new GenericObjectPoolConfig<ImmuClient>();
		config.setJmxEnabled(false);
		config.setMinIdle(properties.getMaxSessions() / 2);
		config.setMaxIdle(properties.getMaxSessions());
		config.setMaxTotal(properties.getMaxSessions());
		config.setMaxWait(Duration.ofMillis(100));
		config.setBlockWhenExhausted(true);

		GenericObjectPool<ImmuClient> pool = new GenericObjectPool<ImmuClient>(new ClientObjectFactory(properties), config);

		pool.addObjects(properties.getMaxSessions());

		return pool;
	}
}
