package org.jboss.narayana.kvstore.infinispan;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.context.Flag;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public abstract class InfinispanKVStoreAbstract implements KVStore {

	// Cache that holds the object store
	private final Cache<String, byte[]> objectStore;
	private final CacheContainer manager;

	protected Flag[] flags = { Flag.SKIP_CACHE_LOAD, Flag.SKIP_REMOTE_LOOKUP };

	private final int SIZE;
	private final String scopePrefix = BeanPopulator.getDefaultInstance(
			CoreEnvironmentBean.class).getNodeIdentifier() + "_";
	private AtomicBoolean[] ids;

	/**
	 * Constructors to allow different cache container and cache names to be
	 * provided.
	 * 
	 * @param container
	 * @param cacheName
	 */
	public InfinispanKVStoreAbstract(CacheContainer container, String cacheName) {
		SIZE = 1024;
		ids = new AtomicBoolean[SIZE];
		manager = container;
		objectStore = manager.getCache(cacheName);
	}

	public InfinispanKVStoreAbstract(CacheContainer container,
			String cacheName, int size) {
		SIZE = size;
		ids = new AtomicBoolean[SIZE];
		manager = container;
		objectStore = manager.getCache(cacheName);
	}

	public InfinispanKVStoreAbstract(String cacheName, String cfgFile) {
		SIZE = 1024;
		ids = new AtomicBoolean[SIZE];
		DefaultCacheManager manager;
		try {
			manager = new DefaultCacheManager(cfgFile);
		} catch (Exception e) {
			throw new RuntimeException("Invalid ConfigFile");
		}
		objectStore = manager.getCache(cacheName);
		this.manager = manager;
		System.out.printf("Testing Cluster Size: %d%n",
				manager.getClusterSize());
		System.out.println(this.getStoreName());
	}

	public InfinispanKVStoreAbstract(ConfigurationBuilder cb, String cacheName,
			String cfgFile) {
		SIZE = 1024;
		ids = new AtomicBoolean[SIZE];
	
		try {
			DefaultCacheManager manager = new DefaultCacheManager(cfgFile);
			manager.defineConfiguration(cacheName, cb.build());
			objectStore = manager.getCache(cacheName);
			this.manager = manager;
			System.out.printf("Testing Cluster Size: %d%n",
					manager.getClusterSize());
		} catch (Exception e) {
			throw new RuntimeException("Invalid ConfigFile");
		}
	}

	// KVStore Methods
	// /////////////////////////////////////

	@Override
	public void start() throws Exception {
		// when starting, assume no keys exist.
		for (int i = 0; i < SIZE; i++) {
			ids[i] = new AtomicBoolean(false);
		}
	}

	/**
	 * Note this method should not stop the entire cache, as that will result in
	 * problems for the application as a whole
	 */
	@Override
	public void stop() throws Exception {
		
	//	System.exit(0);
		System.out.println("Stop Called");
		objectStore.stop();
		manager.stop();
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName() + " " + this;
	}

	@Override
	public void delete(long id) throws Exception {
		if (id < 0)
			throw new Exception("cannot delete: invalid id");
		if (ids[(int) id].compareAndSet(true, false)) {
			objectStore.remove(scopePrefix + id);
			return;
		} 
		else if (noIdsSet()) {
				objectStore.remove(scopePrefix + id);
				return;
		}
		throw new Exception("Index Corrupted");
	}

	@Override
	public void add(long id, byte[] data) throws Exception {
		if (id < 0)
			throw new Exception("cannot put: invalid id");
		// No need to explicitly search for exceptions on put
		// as the calling class will collect exceptions and then throw
		// them up as an ObjectStoreException.
		
		// Time Out of 5s
		objectStore.getAdvancedCache().withFlags(flags)
		.put(scopePrefix + id, data);
		
		/*long timeInterval = System.currentTimeMillis() + 5000;
		boolean timedOut = false;
		do {
			objectStore.getAdvancedCache().withFlags(flags)
				.put(scopePrefix + id, data);
			if (System.currentTimeMillis() >= timeInterval) {
				timedOut = true;
			}
		} while(getStore().getAdvancedCache().getDistributionManager()
				.locate(scopePrefix + id).size() < 3 && ! timedOut);
		if( timedOut ) {
			delete(id);
			throw new Exception("Not enough Replicas Created");
		}*/
	}

	@Override
	public void update(long id, byte[] data) throws Exception {
		if (id < 0)
			throw new Exception("cannot replace: invalid id");
		objectStore.getAdvancedCache().withFlags(flags)
				.replace(scopePrefix + id, data);
	}

	@Override
	/**
	 * Returns the contents of the object store to memory on a local nodeSystem.getProperties()
	 * should be overwritten if anything other than a fully replicated cache
	 * is selected.
	 */
	public List<KVStoreEntry> load() throws Exception {
		if (!objectStore.isEmpty()) {
			LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
			// Get Node IDs for proxy recovery
			for(int i=0; i<SIZE; i++) {
				String key = scopePrefix + i;
				byte[] txdata;
				// Attempt get objectStore and only place in list if you get something
				if ((txdata = objectStore.get(key)) != null) {
					list.add(new KVStoreEntry(i, txdata));
				}
			}
			return list;
		}
		// Return null if objectstore is empty
		return null;
	}

	@Override
	public long allocateId() throws Exception {
		for (int i = 0; i < SIZE; i++) {
			if (ids[i].compareAndSet(false, true)) {
				return (long) i;
			}
		}
		return -1L;
	}

	// /////////////////

	/*
	 * Iterates through the ids array and returns true if
	 * there are none being used. Used to throw an exception
	 * if a key thought to be in use is tried to be deleted by
	 * mistake
	 */
	private boolean noIdsSet() {
		for(AtomicBoolean b : ids) {
			if(b.get()) return false;
		}
		return true;
	}
	
	protected Cache<String, byte[]> getStore() {
		return objectStore;
	}
	
	protected String getPrefix() {
		return scopePrefix;
	}
	
	// /////////////////////
}