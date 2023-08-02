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

@Profile("immudbkv")
@Configuration
public class ImmuDbKvConfiguration {

	@Bean(destroyMethod = "close")
	public ObjectPool<ImmuClient> immuClientPool(ClientProperties properties) {
		GenericObjectPoolConfig<ImmuClient> config = new GenericObjectPoolConfig<ImmuClient>();
		config.setJmxEnabled(false);
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWait(Duration.ofMillis(100));
		config.setBlockWhenExhausted(true);

		ObjectPool<ImmuClient> pool = new GenericObjectPool<ImmuClient>(new ClientObjectFactory(properties), config);

		return pool;
	}
}
