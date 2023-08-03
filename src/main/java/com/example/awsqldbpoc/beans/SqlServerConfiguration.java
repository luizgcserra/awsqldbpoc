package com.example.awsqldbpoc.beans;

import java.sql.Connection;
import java.time.Duration;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.awsqldbpoc.utils.sqlserver.ClientObjectFactory;

@Profile("sqlserver")
@Configuration
public class SqlServerConfiguration {

	@Bean(destroyMethod = "close")
	public ObjectPool<Connection> sqlClientPool(SqlClientProperties properties) throws Exception {
		GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<Connection>();
		config.setJmxEnabled(false);
		config.setMinIdle(properties.getMaxSessions() / 2);
		config.setMaxIdle(properties.getMaxSessions());
		config.setMaxTotal(properties.getMaxSessions());
		config.setMaxWait(Duration.ofSeconds(10));
		config.setBlockWhenExhausted(true);

		GenericObjectPool<Connection> pool = new GenericObjectPool<Connection>(new ClientObjectFactory(properties),
				config);

		pool.addObjects(properties.getMaxSessions());

		return pool;
	}

}
