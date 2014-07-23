package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class DistributedStore extends InfinispanKVStore implements KeyCacheAPI {

	private final String CONFIG_FILE = "generic-test-cfg.xml";
	private final String CACHE_NAME = "dis";

	private Cache<String, Boolean> keyCache;

	public DistributedStore() {
		super();
		keyCache = manager.getCache("key-cache");
		if (keyCache.containsKey(scopePrefix())) {
			// Probably not necessary as at the moment the store simply
			// returns all keys.
			throw new RuntimeException("ScopePrefix Already Exists");
		}
		// Sadly Infinispan will not allow the storage of null values.
		// Done this way to avoid
		keyCache.putIfAbsent(scopePrefix(), false);
	}
	
	@Override
	protected DefaultCacheManager setManager() throws IOException {
		return new DefaultCacheManager(CONFIG_FILE);
	}

	@Override
	protected Cache<String, byte[]> setCache() {
		return manager.getCache(CACHE_NAME);
	}

	@Override
	public void start() throws Exception {
		super.start();
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
		Set<String> deadScope = getDeadMembers();
		for (String scope : scopeSet) {
			if (scope != scopePrefix()) {
				int cnt = 0;
				for (int i = 0; i < size(); i++) {
					// There will be no keys for our ScopePrefix
					String key = scope + "_" + i;
					if (containsKey(key)) {
						// Stripping all CacheKeys of their scopePrefix
						// to generate an ID, however different scopes can
						// use identical slot addresses, there adjust the
						// address by
						// slotAllocation size every iteration
						list.add(new KVStoreEntry(Long.parseLong(key
								.substring(key.lastIndexOf('_') + 1)), get(key)));
						// count number of keys that are associated with
						// down address.
						if(deadScope.contains(scope)) cnt++;
					}
				}
				// If down address has no keys associated with it, remove from keyCache
				if(cnt == 0) {
					keyCache.remove(scope);
				}
			}
		}
		return list;
	}

	protected Set<String> getDeadMembers() {
		String regex = "[\\[\\]\\s]";;
		String replacement = "";
		// Get clusterMembers returns all members as a String
		
		
		HashSet<String> currentMembers = new HashSet<String>(
				Arrays.asList(getMembersAsString().replaceAll(regex,
						replacement).split(",")));
			
		// Get known cluster members
		// Remove all not supported on returned set, everyhting must be hashSet
		HashSet<String> returnSet = new HashSet<String>(keyCache.keySet());
		System.err.println(returnSet.getClass());
		// Strip currently up members from key.
		returnSet.removeAll(currentMembers);
		System.out.println(returnSet + "--" + currentMembers);
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
	

}
