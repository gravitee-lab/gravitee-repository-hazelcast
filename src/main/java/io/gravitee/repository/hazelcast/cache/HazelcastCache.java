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

import com.hazelcast.core.IMap;
import io.gravitee.repository.cache.model.Cache;
import io.gravitee.repository.cache.model.Element;
import io.gravitee.repository.exceptions.CacheException;

import java.util.concurrent.TimeUnit;

public class HazelcastCache implements Cache {
	private IMap<Object, Object> cache;

	public HazelcastCache(IMap<Object, Object> cache) {
		this.cache = cache;
	}

	@Override
	public String getName() {
		return cache.getName();
	}

	@Override
	public Object getNativeCache() {
		return cache;
	}

	@Override
	public Element get(Object key) {
		try {
			Object value = cache.get(key);
			return value == null ? null : Element.from(key, value);
		} catch (RuntimeException e) {
			throw new CacheException("Unexpected error from Hazelcast", e);
		}
	}

	@Override
	public void put(Element element) {
		try {
			if (element.timeToLive() <= 0) {
				cache.put(element.key(), element.value());
			}
			cache.put(element.key(), element.value(), element.timeToLive(), TimeUnit.SECONDS);
		} catch (RuntimeException e) {
			throw new CacheException("Unexpected error from Hazelcast", e);
		}
	}

	@Override
	public void evict(Object key) {
		try {
			cache.evict(key);
		} catch (RuntimeException e) {
			throw new CacheException("Unexpected error from Hazelcast", e);
		}
	}

	@Override
	public void clear() {
		try {
			cache.clear();
		} catch (RuntimeException e) {
			throw new CacheException("Unexpected error from Hazelcast", e);
		}
	}

}
