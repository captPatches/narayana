package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class ClusteredNodeLearning {

	public static void main(String[] args) {
		
		EmbeddedCacheManager manager = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
				   .transport().defaultTransport()// .addProperty("configurationFile", "jgroups.xml")
				   .build()
		);
		
		manager.defineConfiguration("cluster-cache", new ConfigurationBuilder()
			.clustering().cacheMode(CacheMode.REPL_SYNC)
			.build()
		);
		
		Cache<String, String> rc = manager.getCache();
		
		rc.put("ned", "eddard stark");
		System.out.println(rc.get("ned"));
		
	}

}
