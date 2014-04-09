package org.jboss.narayana.infinispankvstore;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;


/**
 * 
 * First attempt at using replication on 2 virtual nodes running on local machine
 * @author patches
 *
 */
public class SecondInfinispanTest {

	public static void main(String[]args) {
		
		
		try {

			EmbeddedCacheManager manager = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
					   .transport().defaultTransport() //.addProperty("configurationFile", "jgroups.xml")
					   .build()
			);
			manager.defineConfiguration("cluster-cache", new ConfigurationBuilder()
				.clustering().cacheMode(CacheMode.REPL_SYNC)
				.build()
			);
			
			Cache<String, String> c = manager.getCache("cluster-cache");
			
			
		//	Cache<String, String> c = new DefaultCacheManager("infinispan-rep-cfg.xml").getCache();
		//	Cache<String, String> c = new DefaultCacheManager("Infinispan-config.xml").getCache();
			
			c.put("ned", "Eddard Stark");
			c.put("arya", "Arya Stark");
			
			Set<Entry<String, String>> entries = c.entrySet();
			for(Entry<String, String> e : entries) {
				System.out.println(e.getValue());
			}		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	}
	
}
