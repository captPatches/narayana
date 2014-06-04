package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

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
	protected DefaultCacheManager getManager() throws IOException {
		return new DefaultCacheManager();
	}

	@Override
	protected Cache<String, byte[]> getCache(EmbeddedCacheManager manager) {
		// 
		return manager.getCache("Imaginary-Cache");
	}

}
