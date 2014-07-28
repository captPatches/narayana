package org.jboss.narayana.kvstore.infinispan;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

public class InfinispanStoreEntry extends KVStoreEntry {

	private String prefix;
	
	public InfinispanStoreEntry(String prefix, long id, byte[] data) {
		super(id, data);
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
}
