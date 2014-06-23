package org.jboss.narayana.kvstore.infinispan;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;

public class DistributedSizeCheckStore extends InfinispanKVStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	private final String CACHE_NAME = "distributed-cache";
	private final DefaultCacheManager manager;
	private final int minClusterSize;
	
	public DistributedSizeCheckStore() {
		try {
			this.manager = new DefaultCacheManager(CONFIG_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Invalid Cache Configuration File");
		}
		this.minClusterSize = 3;
	}
	
	@Override
	protected DefaultCacheManager getManager() throws IOException {
		return manager;
	}

	@Override
	protected Cache<String, byte[]> getCache(EmbeddedCacheManager manager) {
		return manager.getCache(CACHE_NAME);
	}
	
	@Override
	public void add(long id, byte[] data) throws Exception {
		if(manager.getClusterSize() < minClusterSize) throw new ObjectStoreException("Cluster too small");
		super.add(id, data);
	}
}
