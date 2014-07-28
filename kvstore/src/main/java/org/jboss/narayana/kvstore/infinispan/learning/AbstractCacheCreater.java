package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

public abstract class AbstractCacheCreater {

	private final Cache<String, String> cache;
	private final CacheContainer manager;
	
	public AbstractCacheCreater(String cfgFile, String cacheName) throws Exception {
		manager = new DefaultCacheManager(cfgFile);
		cache = manager.getCache("cacheName");
	}
	
	public String put(String key, String value) {
		return cache.put(key, value);
	}
	
	public String get(String key) {
		return cache.get(key);
	}
	
	public CacheContainer manager() {
		return manager;
	}
		
}
