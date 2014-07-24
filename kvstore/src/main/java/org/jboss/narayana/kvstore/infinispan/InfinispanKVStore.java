package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

@Deprecated
public abstract class InfinispanKVStore implements KVStore {

	private String scopePrefix;
	private final DefaultCacheManager manager;
	//private final DecoratedCache<String, byte[]> c;
	private final Cache<String, byte[]> c;

	private static final int SIZE = 2048;

	private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false
																			// =
																			// unallocated,
																			// true
																			// =
																			// allocated
	// holds the key for each record.
	private final String[] keys = new String[SIZE];

	public InfinispanKVStore() {
		// try to cache first, no point in setting up arrays
		// just to discover there is no CacheManager
		try {
			this.manager = setManager();
			
		} catch (IOException e) {
			throw new RuntimeException("ObjectStore Cache Manager Unavailble");
		}
	/*	c = new DecoratedCache<String, byte[]>(setCache(manager)
				.getAdvancedCache(), Flag.IGNORE_RETURN_VALUES,
				Flag.SKIP_CACHE_LOAD);
	*/
		c = setCache();
		scopePrefix = getHostname();
		System.out.println("Cluster Size: "+manager.getClusterSize());
		//c.addListener(new NodeListener());
	}

	@Override
	public void start() throws Exception {

		for (int i = 0; i < slotAllocation.length; i++) {
			slotAllocation[i] = new AtomicBoolean(false);
			keys[i] = scopePrefix + "_" + i;
		}
	}

	@Override
	public void stop() throws Exception {
		manager.stop();
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName() + " " + this;
	}

	@Override
	public void delete(long id) throws Exception {
		c.remove(keys[(int) id]);
		slotAllocation[(int) id].set(false);
	}

	@Override
	public void add(long id, byte[] data) throws Exception {
		c.put(keys[(int) id], data);
	}

	@Override
	public void update(long id, byte[] data) throws Exception {
		c.replace(keys[(int) id], data);
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {
		if (!c.isEmpty()) {
			LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
			for (String key : c.keySet()) {
				// Hostname_ needs to be removed from key, the resulting number
				// String
				// parsed to find a usable Id
				list.add(new KVStoreEntry(Long.parseLong(key.substring(key
						.lastIndexOf('_') + 1)), c.get(key)));
			}
			return list;
		}
		// Return null if objectstore is empty
		return null;
	}

	@Override
	public long allocateId() throws Exception {
		for (int i = 0; i < SIZE; i++) {
			if (!slotAllocation[i].get()) {
				if (slotAllocation[i].compareAndSet(false, true)) {
					return (long) i;
				}
			}
		}
		return -1L;
	}

	/**
	 * Returns the hostname for the box this will be running on
	 * 
	 * @return
	 */
	protected String getHostname() {
		return manager.getAddress().toString();
	}

	protected final DefaultCacheManager getManager() {
		return manager;
	}

	/**
	 * Configures and provides the cache the
	 * 
	 * @return
	 */
	protected abstract DefaultCacheManager setManager() throws IOException;

	/**
	 * Returns the user's cache, takes in the pre-defined cache manager. This
	 * allows subclass to define cache programmatically if desired rather than
	 * relying on XML config files.
	 * 
	 * @return
	 */
	protected abstract Cache<String, byte[]> setCache();
	
	
	protected String scopePrefix() {
		return scopePrefix;
	}
	
	protected byte[] get(String key) {
		return c.get(key);
	}
	
	protected boolean containsKey(String key) {
		return c.containsKey(key);
	}
	
	protected int size() {
		return SIZE;
	}
	
	protected boolean storeEmpty() {
		return c.isEmpty();
	}
	
	protected String getMembersAsString() {
		return manager.getClusterMembers();
	}
	
	@SuppressWarnings("rawtypes")
	protected Cache getCacheByXML(String cacheName) {
		return manager.getCache(cacheName);
	}
	
	@SuppressWarnings("rawtypes")
	protected Cache getCacheByBuilder(ConfigurationBuilder cb, String cacheName) {
		manager.defineConfiguration(cacheName, cb.build());
		return manager.getCache(cacheName);
	}
}
