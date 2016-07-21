/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.hazelcast.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastClientFactory implements FactoryBean<HazelcastInstance> {

	private final Logger logger = LoggerFactory.getLogger(HazelcastClientFactory.class);

	@Autowired
	private Environment environment;

	private final String propertyPrefix;

	public HazelcastClientFactory(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix + ".hazelcast.";
	}

	private ClientConfig config() {
		String username = readPropertyValue(propertyPrefix + "username");
		String password = readPropertyValue(propertyPrefix + "password");

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setProperty("hazelcast.logging.type", "slf4j");
		if (username != null && password != null) {
			logger.info("Logging into hazelcast with user {}", username);
			clientConfig.getGroupConfig().setName(username).setPassword(password);
		} else {
			logger.info("Will login anonymously into hazelcast");
		}

		List<String> addresses = findServerAddress();
		if (addresses.isEmpty()) {
			logger.error("No address found in hazelcast configuration. Default local hazelcast setup will be used.");
			return null;
		} else {
			clientConfig.getNetworkConfig().addAddress(addresses.toArray(new String[] {}));
		}

		Integer connectionTimeout = readPropertyValue(propertyPrefix + "connectionTimeout", Integer.class, 60000);
		Integer connectionAttemptPeriod = readPropertyValue(propertyPrefix + "connectionAttemptPeriod", Integer.class, 3000);
		Integer connectionAttemptLimit = readPropertyValue(propertyPrefix + "connectionAttemptLimit", Integer.class, 2);
		Integer lingerSeconds = readPropertyValue(propertyPrefix + "socketLingerSeconds", Integer.class, 3);
		Integer bufferSize = readPropertyValue(propertyPrefix + "socketBufferSize", Integer.class, 32);

		clientConfig.getNetworkConfig().setConnectionTimeout(connectionTimeout)
				.setConnectionAttemptPeriod(connectionAttemptPeriod).setConnectionAttemptLimit(connectionAttemptLimit)
				.getSocketOptions().setLingerSeconds(lingerSeconds).setBufferSize(bufferSize);

		return clientConfig;
	}

	private List<String> findServerAddress() {
		logger.debug("Looking for Hazelcast addresses...");

		boolean found = true;
		int idx = 0;

		List<String> addresses = new ArrayList<>();

		while (found) {
			String address = environment.getProperty(propertyPrefix + "addresses[" + (idx++) + "]");
			found = (address != null);
			if (found) {
				addresses.add(address);
			}
		}
		return addresses;
	}

	private String readPropertyValue(String propertyName) {
		return readPropertyValue(propertyName, String.class);
	}

	private <T> T readPropertyValue(String propertyName, Class<T> propertyType) {
		return readPropertyValue(propertyName, propertyType, null);
	}

	private <T> T readPropertyValue(String propertyName, Class<T> propertyType, T defaultValue) {
		T value = environment.getProperty(propertyName, propertyType, defaultValue);
		logger.debug("Read property {}: {}", propertyName, value);
		return value;
	}

	@Override
	public HazelcastInstance getObject() throws Exception {
		String configFile = readPropertyValue(propertyPrefix + "configFile");
		if (configFile != null) {
			String mode = readPropertyValue(propertyPrefix + "mode", String.class, "");
			if ("client".equalsIgnoreCase(mode)) {
				ClientConfig cfg = new XmlClientConfigBuilder(configFile).build();
				return HazelcastClient.newHazelcastClient(cfg);
			} else {
				Config cfg = new XmlConfigBuilder(configFile).build();
				return Hazelcast.newHazelcastInstance(cfg);
			}
		}

		ClientConfig config = config();
		if (config != null) {
			return HazelcastClient.newHazelcastClient(config());
		} else {
			return Hazelcast.newHazelcastInstance();
		}
	}

	@Override
	public Class<?> getObjectType() {
		return HazelcastInstance.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
