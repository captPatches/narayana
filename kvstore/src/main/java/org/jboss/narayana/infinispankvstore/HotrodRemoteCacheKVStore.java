package org.jboss.narayana.infinispankvstore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

/**
 * Hotrod client implementation of KVStore.
 * Currently the server has no clustering or replication
 * @author James Brealey
 *
 */
public class HotrodRemoteCacheKVStore implements KVStore {

	String scopePrefix = "test_";
	// Setup an Infinispan RemoteCache
	private RemoteCache<String, byte[]> rc;

	private static final int SIZE = 1024;

	private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false = unallocated, true = allocated
	private final String[] keys = new String[SIZE];	
	
	@Override
	public void start() throws Exception {
	
		// Setup configuration for talking to remote cache
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		Configuration config = confBuilder.addServer().host("127.0.0.1").port(11222).build();
		
		//claim remote cache (Currently use the default)
		RemoteCacheManager manager = new RemoteCacheManager(config, true);
		//RemoteCacheManager manager = new RemoteCacheManager(true);
		rc = manager.getCache();

		// Set all slots to "unused"
		for(int i = 0; i < slotAllocation.length; i++) {
			slotAllocation[i] = new AtomicBoolean(false);
			keys[i] = scopePrefix + i;
		}
	}

	@Override
	public void stop() throws Exception {
		
		// Stop cache explicitly
		rc.stop();

	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName()+" "+this;
	}

	@Override
	public void delete(long id) throws Exception {
		
		rc.remove(keys[(int)  id]);
		slotAllocation[(int)  id].set(false);

	}

	@Override
	public void add(long id, byte[] data) throws Exception {
		
		rc.put(keys[(int) id], data);

	}

	@Override
	public void update(long id, byte[] data) throws Exception {
		rc.replace(keys[(int)id], data);
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {
		return new LinkedList<KVStoreEntry>();
	}

	@Override
	public long allocateId() throws Exception {

		// Method stolen from MemCachedKVStore
		for(int i = 0; i < SIZE; i++) {
			if(!slotAllocation[i].get()) {
				if(slotAllocation[i].compareAndSet(false, true)) {
					return (long)i;
				}
			}
		}

		return -1L;
	}

}
