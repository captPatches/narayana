package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * This class tests whether the node needs to add the infinispan node needs to
 * instantiate the cache in order to retain its info in memory
 * 
 * RESULT: The cache does not need to be instantiated, but
 * "getCache(<i>correctCacheName</i>)" does need to be called.
 * 
 * @author patches
 * 
 */
public class InfinispanNodeRepl2 {

	public static void main(String[] args) {

		/*
		 * Original Attempt // Create cache config - Repl_Sync and
		 * fetchInStateMemory#true ConfigurationBuilder cb = new
		 * ConfigurationBuilder();
		 * cb.clustering().stateTransfer().fetchInMemoryState(true);
		 * cb.clustering().cacheMode(CacheMode.REPL_SYNC);
		 * 
		 * Configuration cacheConfig = cb.build();
		 * 
		 * EmbeddedCacheManager manager = new DefaultCacheManager(
		 * GlobalConfigurationBuilder.defaultClusteredBuilder()
		 * .transport().defaultTransport().build());
		 * 
		 * manager.defineConfiguration("cluster-cache", cacheConfig);
		 * 
		 * manager.getCache().addListener(new SimpleListener());
		 * System.out.println("No Instaitated Cache Node up and running");
		 */

		// SimpleListener listener = new SimpleListener();

		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport().defaultTransport().build());

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.REPL_SYNC);
		cb.clustering().stateTransfer().fetchInMemoryState(true);

		manager.defineConfiguration("cluster-cache", cb.build());

		manager.getCache("cluster-cache");

		// Cache<String, String> c = manager.getCache("cluster-cache");
		// c.addListener(listener);

		// c.addListener(listener);
		System.out.println("Node Up and Running");

	}

}
