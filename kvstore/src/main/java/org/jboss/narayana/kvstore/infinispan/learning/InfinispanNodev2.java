package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 
 * Class that contains the replicated Data as a part off the memory store.
 * 
 * @author patches
 * 
 */
public class InfinispanNodev2 {

	private static final CacheMode MODE = CacheMode.REPL_SYNC;

	public static void main(String[] args) {

		// Programmatically define cache.
		// Define Cache Configuration - use replicated Synchronous Cache, and
		// fetch in memory state true
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.clustering().stateTransfer().fetchInMemoryState(true);
		configBuilder.clustering().cacheMode(MODE);

		Configuration cacheConfig = configBuilder.build();

		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport().defaultTransport().build());

		manager.defineConfiguration("cluster-cache", cacheConfig);

		manager.getCache("cluster-cache");

	}

}
