package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputBuffer;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputBuffer;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.IntermediateStore;

public class InfinispanDirectStore implements IntermediateStore {

	private DefaultCacheManager manager;
	private Cache<Uid, byte[]> objectStore;

	public InfinispanDirectStore() throws IOException {
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		objectStore = manager.getCache("dis");
		System.out.printf("Test Cluster Size: %d%n", manager.getClusterSize());
		System.out.println(this.getStoreName());
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

		objectStore.remove(uid);
		return true;
	}

	@Override
	public boolean write_committed(Uid uid, String typeName,
			OutputObjectState txData) throws ObjectStoreException {
		try {
			OutputBuffer oBuffer = new OutputBuffer();
			oBuffer.packString(typeName);
			oBuffer.packBytes(txData.buffer());
			objectStore.getAdvancedCache()
					.withFlags(Flag.SKIP_REMOTE_LOOKUP, Flag.SKIP_CACHE_LOAD)
					.put(uid, oBuffer.buffer());
			return true;
		} catch (IOException e) {
			throw new ObjectStoreException("WRITE COMMIT FAILED");
		}
	}

	@Override
	public InputObjectState read_committed(Uid uid, String typeName)
			throws ObjectStoreException {

		try {
			byte[] txdata = objectStore.get(uid);
			InputBuffer inputBuffer = new InputBuffer(txdata);
			String retrievedTypeName = inputBuffer.unpackString();
			// Make sure everything make sense
			if (!typeName.equals(retrievedTypeName)) {
				throw new ObjectStoreException("Incorrect Typename Requested");
			}
			byte[] txData = inputBuffer.unpackBytes();
			return new InputObjectState(uid, typeName, txData);
		} catch (IOException e) {
			throw new ObjectStoreException(e);
		}
	}

	@Override
	public Uid[] getUidsForType(String typeName) {
		//Set<Uid> uids = objectStore.keySet();
		Set<Uid> uids = objectStore.keySet();
		//for (Uid uid : uids) {
		for (Uid uid : uids) {
			byte[] txdata = objectStore.get(uid);
			InputBuffer ibuffer = new InputBuffer(txdata);
			String name;
			try {
				name = ibuffer.unpackString();
			} catch (IOException ioe) {
				name = null;
			}
			if (!name.equals(typeName)) {
				uids.remove(uid);
			}
		}
		return uids.toArray(new Uid[uids.size()]);
	}

	@Override
	public String[] getKnownTypes() {

		Set<Uid> keySet = objectStore.keySet();
		Set<String> typeSet = new HashSet<String>();
		byte[] txdata;
		for (Uid key : keySet) {
			txdata = objectStore.get(key);
			InputBuffer buffer = new InputBuffer(txdata);
			String name;
			try {
				name = buffer.unpackString();
			} catch (IOException e) {
				throw new RuntimeException("get known types fail");
			}
			if (name != null) {
				typeSet.add(name);
			}
		}
		return typeSet.toArray(new String[typeSet.size()]);
	}

	@Override
	public boolean contains(Uid uid, String typeName) {
		return objectStore.containsKey(uid);
	}
}
