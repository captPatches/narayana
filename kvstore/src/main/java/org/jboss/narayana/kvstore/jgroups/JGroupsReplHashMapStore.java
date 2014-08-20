package org.jboss.narayana.kvstore.jgroups;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class JGroupsReplHashMapStore implements KVStore {

	private final int SIZE = 1024;
	private final ReplicatedHashMap<String, byte[]> storeMap;
	private AtomicBoolean[] slots = new AtomicBoolean[SIZE];
	private String[] keys = new String[SIZE];
	private String prefix = "_testhost";
	

	public JGroupsReplHashMapStore() throws Exception {
		JChannel channel = new JChannel("jgroups-udp-cfg.xml");
		storeMap = new ReplicatedHashMap<String, byte[]>(channel);
		channel.connect("130489331-ObjectStoreCluster");
	}

	@Override
	public void start() throws Exception {

		storeMap.start(0);
		
		for (int i = 0; i < SIZE; i++) {
			slots[i] = new AtomicBoolean(false);
			keys[i] = prefix + i;
		}
	}

	@Override
	public void stop() throws Exception {
		storeMap.stop();
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName() + " " + this;
	}

	@Override
	public void delete(long id) throws Exception {
		storeMap.remove(keys[(int) id]);
		slots[(int) id].set(false);
	}

	@Override
	public void add(long id, byte[] data) throws Exception {
		storeMap.put(keys[(int) id], data);
	}

	@Override
	public void update(long id, byte[] data) throws Exception {
		storeMap.put(keys[(int) id], data);
	}

	@Override
	public List<KVStoreEntry> load() throws Exception {
		if (!storeMap.isEmpty()) {
			LinkedList<KVStoreEntry> list = new LinkedList<KVStoreEntry>();
			// Get Node IDs for proxy recovery
			for(int i=0; i<SIZE; i++) {
				String key = prefix + i;
				byte[] txdata;
				// Attempt get objectStore and only place in list if you get something
				if ((txdata = storeMap.get(key)) != null) {
					list.add(new KVStoreEntry(i, txdata));
					slots[i].set(true);
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
			if (!slots[i].get()) {
				if (slots[i].compareAndSet(false, true)) {
					return (long) i;
				}
			}
		}
		return -1L;
	}
}
