package org.jboss.narayana.kvstore.infinispan;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class DistributedStore extends InfinispanKVStoreAbstract implements KeyCacheAPI {

	private final static String CONFIG_FILE = "multi-cache-cfg.xml";
	private final static String CACHE_NAME = "distribution";

	private Cache<String, Boolean> keyCache;

	public DistributedStore() {
		super(CACHE_NAME, CONFIG_FILE);
		
		String cacheName = "keyCache";
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.REPL_SYNC);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		manager().defineConfiguration(cacheName, cb.build());
		keyCache = manager().getCache(cacheName);
		
		keyCache.putIfAbsent(scopePrefix(), false);
	}
	
	@Override
	public void start() throws Exception {
		super.start();
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {

		// Test to see if there is anything in the store to start with
		if (objectStoreEmpty()) {
			return null;
		}

		LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
		// This works due to being a part of a
		// fully replicated cache with the fetching of in memory state
		// this the keyset will be the same on every node
		Set<String> scopeSet = keyCache.keySet();
		Set<String> deadScope = getDeadMembers();
		for (String scope : scopeSet) {
			if ( !scope.equals(scopePrefix()) ) {
				int cnt = 0;
				for (int i = 0; i < getMaxId(); i++) {
					// There will be no keys for our ScopePrefix
					String key = scope + "_" + i;
					if (objectStoreContains(key)) {
						// Stripping all CacheKeys of their scopePrefix
						// to generate an ID, however different scopes can
						// use identical slot addresses, there adjust the
						// address by
						// slotAllocation size every iteration
						list.add(new KVStoreEntry(Long.parseLong(key
								.substring(key.lastIndexOf('_') + 1)), getFromStore(key)));
						// count number of keys that are associated with
						// down address.
						cnt++;
					}
				}
				// If down address has no keys associated with it, remove from keyCache
				if(cnt == 0 && deadScope.contains(scope)) {
					keyCache.remove(scope);
				}
			}
		}
		return list;
	}

	private int getMaxId() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected Set<String> getDeadMembers() {
		String regex = "[\\[\\]\\s]";;
		String replacement = "";
		
		// Get clusterMembers returns all members as a String
		HashSet<String> currentMembers = new HashSet<String>(
				Arrays.asList(manager().getClusterMembers().replaceAll(regex,
						replacement).split(",")));
			
		// Get known cluster members
		// Remove all not supported on returned set, everyhting must be hashSet
		HashSet<String> returnSet = new HashSet<String>(keyCache.keySet());
		// Strip currently up members from key.
		returnSet.removeAll(currentMembers);
		return returnSet;
	}

	@Override
	public boolean addKey(String key) {
		if(key == null || key == "") { 
			return false;
		}
		keyCache.put(key, false);
		return true;
	}

	@Override
	public boolean removeKey(String key) {
		if(key == null || key == "") {
			return false;
		}
		return keyCache.remove(key);
	}

	@Override
	public Set<String> allKeys() {
		return keyCache.keySet();
	}

	@Override
	public boolean contains(String key) {
		return keyCache.containsKey(key);
	}
	
	private DefaultCacheManager manager() {
		return (DefaultCacheManager) getManager();
	}
	
}
