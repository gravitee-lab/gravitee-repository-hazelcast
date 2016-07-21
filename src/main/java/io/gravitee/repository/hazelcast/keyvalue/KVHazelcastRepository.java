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
package io.gravitee.repository.hazelcast.keyvalue;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.gravitee.repository.Scope;
import io.gravitee.repository.exceptions.KeyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KVHazelcastRepository implements io.gravitee.repository.keyvalue.api.KeyValueRepository {

	@Autowired
	@Qualifier("managementHazelcast")
	private HazelcastInstance hazelcast;

	@Autowired
	private Environment environment;

	private String cacheName() {
		return environment.getProperty(Scope.KEY_VALUE.getName() + ".hazelcast.map", String.class, "key_value");
	}

	private IMap<String, Object> getMap() {
		try {
			return hazelcast.getMap(cacheName());
		} catch (RuntimeException e) {
			throw new KeyValueException(e);
		}
	}

	@Override
	public Object get(String key) {
		try {
			return getMap().get(key);
		} catch (RuntimeException e) {
			throw new KeyValueException(e);
		}
	}

	@Override
	public Object put(String key, Object value) {
		try {
			return getMap().put(key, value);
		} catch (RuntimeException e) {
			throw new KeyValueException(e);
		}
	}

	@Override
	public Object put(String key, Object value, long ttl) {
		return put(key, value, ttl, TimeUnit.SECONDS);
	}

	@Override
	public Object put(String key, Object value, long ttl, TimeUnit ttlUnit) {
		try {
			return getMap().put(key, value, ttl, ttlUnit);
		} catch (RuntimeException e) {
			throw new KeyValueException(e);
		}
	}
}
