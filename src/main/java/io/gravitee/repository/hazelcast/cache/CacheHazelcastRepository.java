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
package io.gravitee.repository.hazelcast.cache;

import com.hazelcast.core.HazelcastInstance;
import io.gravitee.repository.cache.api.CacheManager;
import io.gravitee.repository.cache.model.Cache;
import io.gravitee.repository.exceptions.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CacheHazelcastRepository implements CacheManager {

	@Autowired
	@Qualifier("managementCacheHazelcast")
	private HazelcastInstance hazelcast;

	@Override
	public Cache getCache(String name) {
		try {
			return new HazelcastCache(hazelcast.getMap(name));
		} catch (RuntimeException e) {
			throw new CacheException("Unexpected error from Hazelcast", e);
		}
	}
}
