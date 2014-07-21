package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.transport.Address;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class DistributedStore extends InfinispanKVStore {

	private final String CONFIG_FILE = "generic-test-cfg.xml";
	private final String CACHE_NAME = "dis";

	private Cache<Boolean, HashSet<String>> keyCache;

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

		// Check to see if a key already exists
		// If so don't overwrite!!
		if (!keyCache.containsKey(true)) {
			keyCache.put(true, new HashSet<String>());
		}
		return manager.getCache(CACHE_NAME);
	}

	@Override
	public void start() throws Exception {
		super.start();
		keyCache.get(true).add(scopePrefix());
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {

		// List<String> keysToReturn = new LinkedList<String>();

		// Get set of member address Strings
		Set<String> members = new HashSet<String>();
		for (Address addr : keyCache.getCacheManager().getMembers()) {
			members.add(addr.toString());
		}

		// Remove all members that are up
		members.removeAll(keyCache.get(true));
		// If all are running, do nothing
		if (members.size() == 0)
			return null;

		// For every member that needs recovering, generate a keyset
		// and append to the returning list.
		ArrayList<String> keySet = new ArrayList<String>();

		for (String key : members) {
			for (int i = 0; i < size(); i++) {
				if (containsKey(key + i)) {
					keySet.add(key + i);
				}
			}
		}

		// Finally return list KVStoreEntries
		LinkedList<KVStoreEntry> returnList = new LinkedList<KVStoreEntry>();
		for (String key : keySet) {
			Long id = Long.parseLong(key.substring(key.lastIndexOf('_'+1))) * key.indexOf(key);
			returnList.add(new KVStoreEntry(id, get(key)));
		}
		return returnList;
	}
}
