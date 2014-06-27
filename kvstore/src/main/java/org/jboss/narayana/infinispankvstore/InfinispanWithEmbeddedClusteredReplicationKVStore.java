package org.jboss.narayana.infinispankvstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class InfinispanWithEmbeddedClusteredReplicationKVStore implements
		KVStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	private final String CACHE_NAME = "replication-cache";
	

	private final String scopePrefix = getHostname();
	// Setup an Infinispan cache
	private EmbeddedCacheManager manager;
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
			manager = new DefaultCacheManager(CONFIG_FILE);
			c = manager.getCache(CACHE_NAME);
		} catch (IOException e) {
			throw new RuntimeException("No Cache Availble");
		}
		// Set all slots to "unused"
		for (int i = 0; i < slotAllocation.length; i++) {
			slotAllocation[i] = new AtomicBoolean(false);
			keys[i] = scopePrefix + i;
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
				// parsed to find a useable Id
				list.add(new KVStoreEntry(Long.parseLong(key.substring(key.lastIndexOf('_')+1)), c.get(key)));
			}
			return list;
		} else {
			// If ObjectStore is empty then return null.
			return null;
		}
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
	 * TODO:  Test portability of this code to windows and BSD?!?
	 * @return
	 */
	private String getHostname() {
		
		String hostname = null;
		
		try {
			// Not run as a script to aid portability.
			Process p = Runtime.getRuntime().exec("hostname");
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// Should be only one line - could check and throw exception if there
			// is more than one line returned.  Complexity probably hurts portability however.
			hostname = reader.readLine();
			reader.close();
		}
		catch(IOException ioe) {
			hostname = "Default_Hostname_";
		}
		
		return hostname + "_";
	}
}
