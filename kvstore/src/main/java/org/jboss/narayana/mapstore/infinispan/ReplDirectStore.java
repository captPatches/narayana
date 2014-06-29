package org.jboss.narayana.mapstore.infinispan;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;

public class ReplDirectStore extends InfinispanDirectStore {

	private final String CONFIG_FILE = "multi-cache-cfg.xml";
	
	@Override
	protected Cache<Uid, CacheEntry> setCache(
			DefaultCacheManager manager) throws Exception {
		
		return manager.getCache("replication-cache");
	}
	

	@Override
	protected DefaultCacheManager setManager() throws Exception {
		try {
			return new DefaultCacheManager(CONFIG_FILE);
		} catch (Exception e) {
			throw new ObjectStoreException("No Cache Availble");
		}
	}
	

}
