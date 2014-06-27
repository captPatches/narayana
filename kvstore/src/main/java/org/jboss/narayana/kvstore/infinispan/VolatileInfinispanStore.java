package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Uses an infinispan cache rather than Hash map for the object store,
 * however will attempt no replication of data.  Useful to get a baseline
 * for performance tests.
 * 
 * @author patches
 *
 */
public class VolatileInfinispanStore extends InfinispanKVStore {

	@Override
	protected DefaultCacheManager setManager() throws IOException {
		return new DefaultCacheManager();
	}

	@Override
	protected Cache<String, byte[]> setCache(DefaultCacheManager manager) {
		// 
		return manager.getCache("Imaginary-Cache");
	}

}
