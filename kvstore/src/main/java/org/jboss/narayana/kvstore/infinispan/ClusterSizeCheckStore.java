package org.jboss.narayana.kvstore.infinispan;

import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;

public abstract class ClusterSizeCheckStore extends InfinispanKVStoreAbstract {
	
	public ClusterSizeCheckStore(DefaultCacheManager manager, String cacheName) {
		super(manager, cacheName, "ned");
	}
	
	@Override
	public final void add(long id, byte[] data) throws Exception {
		if(manager().getClusterSize() < 3) throw new ObjectStoreException("Not Enough Replicas");
		super.add(id, data);
	}

	private DefaultCacheManager manager() {
		return (DefaultCacheManager) getManager();
	}
}
