package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class DistributedSizeCheckStore extends ClusterSizeCheckStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	private final String CACHE_NAME = "distributed-cache";
		
	@Override
	protected Cache<String, byte[]> setCache(DefaultCacheManager manager) {
		return manager.getCache(CACHE_NAME);
	}
	
	@Override
	protected DefaultCacheManager setManager() throws IOException {
		return new DefaultCacheManager(CONFIG_FILE);
	}
}
