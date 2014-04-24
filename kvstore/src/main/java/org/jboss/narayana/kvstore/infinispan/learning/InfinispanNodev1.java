package org.jboss.narayana.kvstore.infinispan.learning;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;


public class InfinispanNodev1 {

	public static void main(String[] args) {
		
		SimpleListener listener = new SimpleListener();
		
		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder
						.defaultClusteredBuilder()
						.transport()
						.defaultTransport()
						.addProperty("configurationFile", "jgroups-udp.xml")
						//.addProperty("clusterName", "clustered")
						.build());
	//	manager.addListener(listener);
		manager.defineConfiguration(
				"cluster-cache",
				new ConfigurationBuilder().clustering()
						.cacheMode(CacheMode.REPL_SYNC).build());
		

		Cache<String, String> c = manager.getCache("cluster-cache");
		
		c.addListener(listener);

	}

}
