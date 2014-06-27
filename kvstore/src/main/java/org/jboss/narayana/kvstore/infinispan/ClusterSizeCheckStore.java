package org.jboss.narayana.kvstore.infinispan;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;

public abstract class ClusterSizeCheckStore extends InfinispanKVStore {
	
	@Override
	public final void add(long id, byte[] data) throws Exception {
		if(getManager().getClusterSize() < 3) throw new ObjectStoreException("Not Enough Replicas");
		super.add(id, data);
	}

}
