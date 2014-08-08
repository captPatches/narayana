package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.IntermediateStore;

public class KVInfinispanMapperCacheEntry implements IntermediateStore {

	private DefaultCacheManager manager;
	private Cache<Uid, CacheEntry> objectStore;
	
	public KVInfinispanMapperCacheEntry() throws IOException {
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		objectStore = manager.getCache("dis");
		System.out.printf("Test Cluster Size: %d%n", manager.getClusterSize());
	}
	
	@Override
	public void start() throws Exception {}

	
	@Override
	public void stop() throws Exception {
		manager.stop();
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName() + this;
	}

	@Override
	public boolean remove_committed(Uid uid, String typeName)
			throws ObjectStoreException {
		
		objectStore.remove(uid);
		return true;
	}

	@Override
	public boolean write_committed(Uid uid, String typeName,
			OutputObjectState txData) throws ObjectStoreException {
		
		objectStore.getAdvancedCache().withFlags(Flag.SKIP_REMOTE_LOOKUP, Flag.SKIP_CACHE_LOAD).put(uid, new CacheEntry(typeName, txData.buffer()));
		return true;
	}

	@Override
	public InputObjectState read_committed(Uid uid, String typeName)
			throws ObjectStoreException {
		
		CacheEntry entry = objectStore.get(uid);
		if( ! entry.getType().equals(typeName) ) {
			throw new ObjectStoreException("Incorrect Type Associated With Uid");
		}
		byte[] buffer = entry.getTxData();
		return new InputObjectState(uid, typeName, buffer);
	}

	@Override
	public Uid[] getUidsForType(String typeName) {
		LinkedList<Uid> uids = new LinkedList<Uid>();
		for(Uid uid : objectStore.keySet()) {
			if(objectStore.get(uid).getType().equals(typeName)) {
				uids.add(uid);
			}
		}
		return uids.toArray(new Uid[uids.size()]);
	}

	@Override
	public String[] getKnownTypes() {
		
		Set<Uid> keySet = objectStore.keySet();
		Set<String> typeSet = new HashSet<String>();
		for(Uid key : keySet) {
			typeSet.add(objectStore.get(key).getType());
		}
		return typeSet.toArray(new String[typeSet.size()]);
	}

	@Override
	public boolean contains(Uid uid, String typeName) {
		return objectStore.containsKey(uid);
	}
}


