package com.example.awsqldbpoc.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MicrometerConfiguration {

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> configureCommonTags() {
		return registry -> registry.config().commonTags(new String[] { "profile", activeProfile });
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry meterRegistry) {
		return new TimedAspect(meterRegistry);
	}

}
