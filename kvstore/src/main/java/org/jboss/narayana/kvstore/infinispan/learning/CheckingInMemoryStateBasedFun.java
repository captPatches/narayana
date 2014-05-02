package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CheckingInMemoryStateBasedFun {

	// private StateTransferConfigurationBuilder stateConfing =
	// StateTransferConfigurationBuilder.fetchInMemoryState(true);

	// new StateTransferConfigurationBuilder(null);

	public static void main(String[] args) {
		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport().defaultTransport().build());

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.clustering().cacheMode(CacheMode.REPL_SYNC);
		cb.clustering().stateTransfer().fetchInMemoryState(true);
		Configuration config = cb.build();

		manager.defineConfiguration("cluster-cache", config);

		Cache<String, String> c = manager.getCache("cluster-cache");
		c.addListener(new SimpleListener());

		System.out.println("Node Up");

		if (!c.isEmpty()) {
			for (String s : c.keySet()) {
				System.out.println(c.get(s));
			}
		} else {
			System.out.println("This hasn't worked!!");
		}
	}

}
