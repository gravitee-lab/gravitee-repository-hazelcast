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
package io.gravitee.repository.hazelcast;

import io.gravitee.repository.Repository;
import io.gravitee.repository.Scope;
import io.gravitee.repository.hazelcast.cache.CacheHazelcastRepoConfiguration;
import io.gravitee.repository.hazelcast.keyvalue.KVHazelcastRepositoryConfiguration;

public class HazelcastRepository implements Repository {

    @Override
    public String type() {
		return "hazelcast";
    }

    @Override
    public Scope[] scopes() {
        return new Scope [] {
				Scope.KEY_VALUE,
				Scope.CACHE
        };
    }
    @Override
    public Class<?> configuration(Scope scope) {
		// Until hazelcast 3.7 the only reliable way to have slf4j log is this...
		System.setProperty("hazelcast.logging.type", "slf4j");

        switch (scope) {
		case KEY_VALUE:
			return KVHazelcastRepositoryConfiguration.class;
		case CACHE:
			return CacheHazelcastRepoConfiguration.class;
		default:
			return null;

        }
    }
}
