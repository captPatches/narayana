package org.jboss.narayana.kvstore.infinispan.mill;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.narayana.kvstore.infinispan.ClusterSizeCheckStore;

public class MillDistSizeCheckStore extends ClusterSizeCheckStore {
	
	private String CACHE_NAME = "dist-cache";
	private String CONFIG_FILE = "configlib/jgroups-tcp-mill002-cfg.xml";
	
	public DefaultCacheManager setManager() {
		return new DefaultCacheManager(GlobalConfigurationBuilder
				.defaultClusteredBuilder().transport().defaultTransport()
				.addProperty("configurationFile", CONFIG_FILE)
				.addProperty("clusterName", "b3408933-cluster").build());
	}
	

	@Override
	protected Cache<String, byte[]> setCache(DefaultCacheManager manager) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.DIST_SYNC);
		cb.clustering().hash().numOwners(3);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		manager.defineConfiguration(CACHE_NAME, cb.build());
		
		return manager.getCache(CACHE_NAME);
	}
	
	@Override
	protected String getHostname() {
		// remove the '.ncl.ac.uk'.
		try {
			return super.getHostname().substring(0, super.getHostname().indexOf('.')-1);
		} catch (Exception e) {
			System.err.println("hostname unavailble");
			return "default_hostname";
		}
	}

}