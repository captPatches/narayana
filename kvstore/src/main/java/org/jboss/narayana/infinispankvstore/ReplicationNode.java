package org.jboss.narayana.infinispankvstore;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Runs an infinispan node with one cache. That cache is run in replicating
 * synchronous mode anff has fetchInMemoryState ste to true
 * 
 * @author patches
 * 
 */
public class ReplicationNode {

	public static void main(String[] args) {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		cb.clustering().cacheMode(CacheMode.REPL_SYNC);

		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport().defaultTransport().build());

		manager.defineConfiguration("cluster-cache", new ConfigurationBuilder()
				.clustering().cacheMode(CacheMode.REPL_SYNC).build());

		manager.getCache("cluster-cache");

	}

}
