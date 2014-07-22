package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class DistributedStore extends InfinispanKVStore {

	private final String CONFIG_FILE = "generic-test-cfg.xml";
	private final String CACHE_NAME = "dis";

	private Cache<String, Void> keyCache;

	@Override
	protected DefaultCacheManager setManager() throws IOException {
		return new DefaultCacheManager(CONFIG_FILE);
	}

	@Override
	protected Cache<String, byte[]> setCache(DefaultCacheManager manager) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.REPL_SYNC);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		manager.defineConfiguration("key-cache", cb.build());

		keyCache = manager.getCache("key-cache");
		return manager.getCache(CACHE_NAME);
	}

	@Override
	public void start() throws Exception {
		super.start();

		if (keyCache.containsKey(scopePrefix())) {
			throw new RuntimeException("ScopePrefix Already Exists");
		}
		keyCache.put(scopePrefix(), null);
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {

		// Test to see if there is anything in the store to start with
		if (storeEmpty())
			return null;

		LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
		// This works due to being a part of a
		// fully replicated cache with the fetching of in memory state
		// this the keyset will be the same on every node
		Set<String> scopeSet = keyCache.keySet();
		for (String scope : scopeSet) {
			for (int i = 0; i < size(); i++) {
				String key = scope + "_" + i;
				if (containsKey(key))
					;
				list.add(new KVStoreEntry(Long.parseLong(key.substring(key
						.lastIndexOf('_') + 1)), get(key)));
			}
		}
		return list;
	}

}
