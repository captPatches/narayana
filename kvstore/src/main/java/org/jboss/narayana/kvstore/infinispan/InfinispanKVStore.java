package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public abstract class InfinispanKVStore implements KVStore {

	private final String scopePrefix = getHostname();
	private EmbeddedCacheManager manager;
	private Cache<String, byte[]> c;

	private static final int SIZE = 1024;

	private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false
																			// =
																			// unallocated,
																			// true
																			// =
																			// allocated
	// holds the key for each record.
	private final String[] keys = new String[SIZE];
	
	@Override
	public void start() throws Exception {
		// try to cache first, no point in setting up arrays
		// just to discover there is no CacheManager
		try {
			this.manager = getManager();
		} catch (IOException e) {
			throw new ObjectStoreException("Infinispan Configuration Not Availble");
		}
		c = getCache(manager);
		
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
		if(!c.isEmpty()) {
			LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
			for(String key : c.keySet()) {
				// Hostname_ needs to be removed from key, the resulting number String
				// parsed to find a usable Id
				list.add(new KVStoreEntry(Long.parseLong(key.substring(key.lastIndexOf('_')+1)), c.get(key)));
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
		try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (java.net.UnknownHostException e) {
			System.err.println(e.getMessage());
			return "unknown_hostname";
		}
	}

	/**
	 * Configures and provides the cache the
	 * 
	 * @return
	 */
	protected abstract DefaultCacheManager getManager() throws IOException;
	
	/**
	 * Returns the user's cache, takes in the pre-defined cache manager.
	 * This allows subclass to define cache programmatically if desired
	 * rather than relying on XML config files.
	 * @return
	 */
	protected abstract Cache<String, byte[]> getCache(EmbeddedCacheManager manager);
}
