package org.jboss.narayana.infinispankvstore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class InfinispanWithEmbeddedClusteredReplicationKVStore implements KVStore {

	private final CacheMode MODE = CacheMode.REPL_SYNC;
	
	String scopePrefix = "test_";
	// Setup an Infinispan cache
	private Cache<String, byte[]> c;

	private static final int SIZE = 1024;

	private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false
																			// =
																			// unallocated,
																			// true
																			// =
																			// allocated
	private final String[] keys = new String[SIZE];

	@Override
	public void start() throws Exception {

		
		//Define Cache Configuration - use replicated Synchronous Cache, and fetch in memory state true
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
	//	configBuilder.clustering().stateTransfer().fetchInMemoryState(true);
		configBuilder.clustering().cacheMode(MODE);
		
		Configuration cacheConfig = configBuilder.build();
		
		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport().defaultTransport().build());

		manager.defineConfiguration("cluster-cache", cacheConfig);

		c = manager.getCache();

		// Set all slots to "unused"
		for (int i = 0; i < slotAllocation.length; i++) {
			slotAllocation[i] = new AtomicBoolean(false);
			keys[i] = scopePrefix + i;
		}
	}

	@Override
	public void stop() throws Exception {

		c.stop();
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName() + " " + this;
	}

	@Override
	public void delete(long id) throws Exception {
		c.remove(id);
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
		return new LinkedList<KVStoreEntry>();
	}

	@Override
	public long allocateId() throws Exception {
		// Method stolen from MemCachedKVStore
		for (int i = 0; i < SIZE; i++) {
			if (!slotAllocation[i].get()) {
				if (slotAllocation[i].compareAndSet(false, true)) {
					return (long) i;
				}
			}
		}
		return -1L;
	}
}
