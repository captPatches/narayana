package org.jboss.narayana.kvstore.infinispan.mill;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.narayana.kvstore.infinispan.InfinispanKVStore;

public class MillDistCacheStore extends InfinispanKVStore {
	
	private String CACHE_NAME = "dist-cache";
	private String CONFIG_FILE = "configlib/jgroups-tcp-mill002-cfg.xml";
	
	@Override
	protected DefaultCacheManager getManager() throws IOException {
		return new DefaultCacheManager(GlobalConfigurationBuilder
				.defaultClusteredBuilder().transport().defaultTransport()
				.addProperty("configurationFile", CONFIG_FILE)
				.addProperty("clusterName", "b3408933-cluster").build());
	}

	@Override
	protected Cache<String, byte[]> getCache(EmbeddedCacheManager manager) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.DIST_SYNC);
		cb.clustering().hash().numOwners(3);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		manager.defineConfiguration(CACHE_NAME, cb.build());
		
		return manager.getCache(CACHE_NAME);
	}
	
	@Override
	protected String getHostname() {
		// try to remove the .ncl.ac.uk that should be attatched.
		try {
			return super.getHostname().substring(0, super.getHostname().indexOf('.')-1);
		} catch (Exception e) {
			System.err.println("hostname unavailble");
			return "default_hostname";
		}
	}

}
