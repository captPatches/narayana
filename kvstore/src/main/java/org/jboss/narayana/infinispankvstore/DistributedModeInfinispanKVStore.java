package org.jboss.narayana.infinispankvstore;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class DistributedModeInfinispanKVStore implements KVStore {

private final String CONFIG_FILE = "multi-cache-cfg.xml";
private final String CACHE_NAME = "distributed-cache";
	
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
		
		try {
		EmbeddedCacheManager manager = new DefaultCacheManager(CONFIG_FILE);

		c = manager.getCache(CACHE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
