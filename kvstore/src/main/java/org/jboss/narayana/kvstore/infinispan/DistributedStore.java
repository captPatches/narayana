package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class DistributedStore extends InfinispanKVStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	private final String CACHE_NAME = "distributed-cache";
	
	@Override
	protected DefaultCacheManager setManager() throws IOException {
		return new DefaultCacheManager(CONFIG_FILE);
	}

	@Override
	protected Cache<String, byte[]> setCache(DefaultCacheManager manager) {
		return manager.getCache(CACHE_NAME);
	}
}
