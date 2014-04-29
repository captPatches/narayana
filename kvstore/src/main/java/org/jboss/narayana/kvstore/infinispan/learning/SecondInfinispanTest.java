package org.jboss.narayana.kvstore.infinispan.learning;

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
 * First attempt at using replication on 2 virtual nodes running on local
 * machine
 * 
 * @author patches
 * 
 */
public class SecondInfinispanTest {

	public static void main(String[] args) {

		try {

			EmbeddedCacheManager manager = new DefaultCacheManager(
					GlobalConfigurationBuilder
							.defaultClusteredBuilder()
							.transport()
							.defaultTransport()
	//						.addProperty("configurationFile", "jgroups-udp.xml")
		//					.addProperty("clusterName", "clustered")
							.build());
	//		manager.addListener(new SimpleListener());
			manager.defineConfiguration(
					"cluster-cache",
					new ConfigurationBuilder().clustering()
							.cacheMode(CacheMode.REPL_SYNC).build());
			

			Cache<String, String> c = manager.getCache("cluster-cache");
			c.addListener(new SimpleListener());
			// Cache<String, String> c = new
			// DefaultCacheManager("infinispan-rep-cfg.xml").getCache();
			// Cache<String, String> c = new
			// DefaultCacheManager("Infinispan-config.xml").getCache();

			String[] keys = {"ned", "arya", "jamie", "tywin"};
			String[] values = {"Eddard Stark", "Arya Stark", "Jamie Lannister","Tywin Lannister"};
			
			for(int i=0;i<4;i++) {
				if(!c.containsKey(keys[i])) {
					c.put(keys[i],  values[i]);
				}
			}

			for(int i=0;i<4;i++) {
				if(!c.containsKey(keys[i])) {
					c.put(keys[i],  values[i]);
				}
			}
			
			c.put("jon", "Jon Snow");
			Set<Entry<String, String>> entries = c.entrySet();
			for (Entry<String, String> e : entries) {
				System.out.println(e.getValue());
			}

			manager.removeCache("cluster-cache");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

	}

}
