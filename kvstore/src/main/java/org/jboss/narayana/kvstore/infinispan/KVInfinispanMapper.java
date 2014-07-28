package org.jboss.narayana.kvstore.infinispan;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputBuffer;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputBuffer;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.IntermediateStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEnvironmentBean;

public class KVInfinispanMapper implements IntermediateStore {

	private final KVStoreInfinispan store;
	private final ConcurrentMap<String, Map<Uid, KVStoreEntry>> content;

	public KVInfinispanMapper(KVStoreEnvironmentBean environmentBean) {
		store = (KVStoreInfinispan) environmentBean.getStoreImplementation();
		content = new ConcurrentHashMap<String, Map<Uid, KVStoreEntry>>();
	}

	@Override
	public void start() throws Exception {
		store.start();
		updateContent();
	}

	@Override
	public void stop() throws Exception {
		store.stop();
	}

	@Override
	public String getStoreName() {
		return store.getStoreName();
	}

	@Override
	public boolean remove_committed(Uid uid, String typeName)
			throws ObjectStoreException {

		try {
			long id = getId(uid, typeName); // look up the id *before* doing the
											// remove from state, or it won't be
											// there any more.
			getContentForType(typeName).remove(uid);
			store.delete(id);
		} catch (Exception e) {
			throw new ObjectStoreException(e);
		}
		return true;
	}

	@Override
	public boolean write_committed(Uid uid, String typeName,
			OutputObjectState txData) throws ObjectStoreException {
		try {
			OutputBuffer outputBuffer = new OutputBuffer();
			UidHelper.packInto(uid, outputBuffer);
			outputBuffer.packString(typeName);
			outputBuffer.packBytes(txData.buffer());
			long id = getId(uid, typeName);
			byte[] data = outputBuffer.buffer();
			
			 if(getContentForType(typeName).containsKey(uid)) {
	                store.update(id, data);
			 } else {
				 store.add(id, data);
			 }
			 
			return true;
		} catch (Exception e) {
			throw new ObjectStoreException(e);
		}
	}

	@Override
	public InputObjectState read_committed(Uid uid, String typeName)
			throws ObjectStoreException {

	}

	@Override
	public Uid[] getUidsForType(String typeName) {
		updateContent();

	}

	/**
	 * This method is a bit of a best effort atm, if the object store is
	 * unreachable there is nothing to stop the method from returning something
	 * atm.
	 */
	@Override
	public String[] getKnownTypes() {
		try {
			updateContent();
		} catch (Exception e) {
			return null;
		}
		return content.keySet().toArray(new String[content.size()]);
	}

	@Override
	public boolean contains(Uid uid, String typeName) {
		if (getContentForType(typeName).get(uid) != null) {
			return true;
		}
		try {
			updateContent();

		} catch (Exception e) {
			// TODO do Something
		}
		return getContentForType(typeName).containsKey(uid);
	}

	private void updateContent() throws ObjectStoreException {

		try {
			List<InfinispanStoreEntry> rawEntries = store.load();
			if (rawEntries.size() == 0 || rawEntries == null) {
				return;
			}
			for (InfinispanStoreEntry entry : rawEntries) {
				InputBuffer inputBuffer = new InputBuffer(entry.getData());
				Uid uid = UidHelper.unpackFrom(inputBuffer);
				String typeName = inputBuffer.unpackString();
				ConcurrentHashMap<Uid, KVStoreEntry> localContent = (ConcurrentHashMap<Uid, KVStoreEntry>) getContentForType(typeName);
				localContent.putIfAbsent(uid, entry);
				content.put(typeName, localContent);
			}
		} catch (Exception e) {
			throw new ObjectStoreException("Unable to read ObjectStore");
		}
	}

	private Map<Uid, KVStoreEntry> getContentForType(String typeName) {
		Map<Uid, KVStoreEntry> result = content.get(typeName);
		if (result == null) {
			content.putIfAbsent(typeName,
					new ConcurrentHashMap<Uid, KVStoreEntry>());
			result = content.get(typeName);
		}
		return result;
	}

	private long getId(Uid uid, String typeName) throws Exception {
		KVStoreEntry kvStoreEntry = getContentForType(typeName).get(uid);
		if (kvStoreEntry == null) {
			updateContent();
			kvStoreEntry = getContentForType(typeName).get(uid);
		}
		if (kvStoreEntry == null) {
			return store.allocateId();
		}
		return kvStoreEntry.getId();
	}

}
