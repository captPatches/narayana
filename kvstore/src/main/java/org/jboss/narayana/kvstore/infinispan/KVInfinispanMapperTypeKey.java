package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.IntermediateStore;

public class KVInfinispanMapperTypeKey implements IntermediateStore {

	private class TypeKeyEntry {

		private Uid uid;
		private byte[] txData;

		public TypeKeyEntry(Uid uid, byte[] txData) {
			this.uid = uid;
			this.txData = txData;
		}

		public Uid getUid() {
			return uid;
		}

		public byte[] getTxData() {
			return txData;
		}
	}

	private DefaultCacheManager manager;
	private Cache<String, Map<Uid, byte[]>> objectStore;

	public KVInfinispanMapperTypeKey() throws IOException {
		manager = new DefaultCacheManager("multi-cache-cfg.xml");
		objectStore = manager.getCache("distribution");
	}

	@Override
	public void start() throws Exception {
	}

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

		;
		return true;
	}

	@Override
	public boolean write_committed(Uid uid, String typeName,
			OutputObjectState txData) throws ObjectStoreException {

		getContentForType(typeName).put(uid, txData.buffer());
		return true;

	}

	@Override
	public InputObjectState read_committed(Uid uid, String typeName)
			throws ObjectStoreException {

		byte[] buffer = getContentForType(typeName).get(uid);
		return new InputObjectState(uid, typeName, buffer);
	}

	@Override
	public Uid[] getUidsForType(String typeName) {
		Set<Uid> uids = getContentForType(typeName).keySet();
		return uids.toArray(new Uid[uids.size()]);
	}

	@Override
	public String[] getKnownTypes() {
		return objectStore.keySet().toArray(
				new String[objectStore.keySet().size()]);
	}

	@Override
	public boolean contains(Uid uid, String typeName) {
		return objectStore.containsKey(uid);
	}

	private Map<Uid, byte[]> getContentForType(String typeName) {
		Map<Uid, byte[]> result = objectStore.get(typeName);
		if (result == null) {
			objectStore.getAdvancedCache().withFlags(Flag.SKIP_CACHE_LOAD)
					.put(typeName, new ConcurrentHashMap<Uid, byte[]>());
			result = objectStore.get(typeName);
		}
		return result;
	}
}
