package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class SubProgramicCache extends ProgramaticCacheTestingBasedFun {

	private final String CONFIG_FILE = "jgroups-tcp-cfg.xml";
	private final String CACHE_NAME = "dist-cache";
	
	@Override
	protected DefaultCacheManager getManager() {
		try {
			return new DefaultCacheManager(GlobalConfigurationBuilder
					.defaultClusteredBuilder().transport().defaultTransport()
					.addProperty("configurationFile", CONFIG_FILE)
					.addProperty("clusterName", "b3408933-cluster").build());
		} catch (Exception e) {
			throw new RuntimeException("Cache Manager usless");
		}
	}
	
	@Override
	protected Cache<String, String> getCache(DefaultCacheManager manager) {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.DIST_SYNC);
		cb.clustering().hash().numOwners(3);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		manager.defineConfiguration(CACHE_NAME, cb.build());

		return manager.getCache(CACHE_NAME);
	}
	
}
