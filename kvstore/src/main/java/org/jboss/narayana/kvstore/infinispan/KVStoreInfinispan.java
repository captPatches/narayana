package org.jboss.narayana.kvstore.infinispan;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;

public interface KVStoreInfinispan extends KVStore {

	public void delete(String prefix, long id) throws Exception;
	
	public String getPrefix();
}
