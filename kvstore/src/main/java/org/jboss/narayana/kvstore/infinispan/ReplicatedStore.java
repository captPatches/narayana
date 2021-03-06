package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public final class ReplicatedStore extends InfinispanKVStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	private final String CACHE_NAME = "replication-cache";
	
	@Override
	protected DefaultCacheManager getManager() throws IOException {
		return new DefaultCacheManager(CONFIG_FILE);
	}
	
	@Override
	protected Cache<String, byte[]> getCache(EmbeddedCacheManager manager) {
		return manager.getCache(CACHE_NAME);
	}
}
