/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.metrics.mongo;

import com.mongodb.MongoClientSettings;
import io.micrometer.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.binder.mongodb.MongoMetricsConnectionPoolListener;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoConfigurations.MongoMetricsCommandListenerConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoConfigurations.MongoMetricsConnectionPoolListenerConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongo metrics.
 *
 * @author Chris Bono
 * @author Jonatan Ivanov
 * @author Moritz Halbritter
 * @since 2.5.0
 */
@AutoConfiguration(before = MongoAutoConfiguration.class,
		after = { MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class })
@ConditionalOnClass(MongoClientSettings.class)
@ConditionalOnBean(MeterRegistry.class)
@SuppressWarnings("deprecation")
public class MongoMetricsAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(MongoMetricsCommandListener.class)
	@ConditionalOnProperty(name = "management.metrics.mongo.command.enabled", havingValue = "true",
			matchIfMissing = true)
	@Import(MongoMetricsCommandListenerConfiguration.class)
	static class MongoCommandMetricsConfiguration {

		@Bean
		@ConditionalOnBean(MongoMetricsCommandListener.class)
		MongoClientSettingsBuilderCustomizer mongoMetricsCommandListenerClientSettingsBuilderCustomizer(
				MongoMetricsCommandListener mongoMetricsCommandListener) {
			return (clientSettingsBuilder) -> clientSettingsBuilder.addCommandListener(mongoMetricsCommandListener);
		}

		@Bean
		@ConditionalOnBean(io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener.class)
		MongoClientSettingsBuilderCustomizer mongoMetricsCommandListenerClientSettingsBuilderCustomizerBackwardsCompatible(
				io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener mongoMetricsCommandListener) {
			return (clientSettingsBuilder) -> clientSettingsBuilder.addCommandListener(mongoMetricsCommandListener);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(MongoMetricsConnectionPoolListener.class)
	@ConditionalOnProperty(name = "management.metrics.mongo.connectionpool.enabled", havingValue = "true",
			matchIfMissing = true)
	@Import(MongoMetricsConnectionPoolListenerConfiguration.class)
	static class MongoConnectionPoolMetricsConfiguration {

		@Bean
		@ConditionalOnBean(MongoMetricsConnectionPoolListener.class)
		MongoClientSettingsBuilderCustomizer mongoMetricsConnectionPoolListenerClientSettingsBuilderCustomizer(
				MongoMetricsConnectionPoolListener mongoMetricsConnectionPoolListener) {
			return (clientSettingsBuilder) -> clientSettingsBuilder
					.applyToConnectionPoolSettings((connectionPoolSettingsBuilder) -> connectionPoolSettingsBuilder
							.addConnectionPoolListener(mongoMetricsConnectionPoolListener));
		}

		@Bean
		@ConditionalOnBean(io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener.class)
		MongoClientSettingsBuilderCustomizer mongoMetricsConnectionPoolListenerClientSettingsBuilderCustomizerBackwardsCompatible(
				io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener mongoMetricsConnectionPoolListener) {
			return (clientSettingsBuilder) -> clientSettingsBuilder
					.applyToConnectionPoolSettings((connectionPoolSettingsBuilder) -> connectionPoolSettingsBuilder
							.addConnectionPoolListener(mongoMetricsConnectionPoolListener));
		}

	}

}
