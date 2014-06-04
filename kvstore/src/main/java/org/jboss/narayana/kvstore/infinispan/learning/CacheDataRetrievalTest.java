package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CacheDataRetrievalTest {

	public static void main(String[]args) throws Exception {
		
		//Ensure use of IP4 Stack
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		//Instantiate a manager
		EmbeddedCacheManager manager = new DefaultCacheManager("multi-cache-cfg.xml");
		
		// Get a cache
		Cache<String, String> c = manager.getCache("replication-cache");
		
		// Then either read data to, or receive data from cache
		if(c.isEmpty()) {
			c.put("ned", "Eddard Stark");
			c.put("dany", "Daenerys Targaryan");
			c.put("eva", "Eva Green");
			c.put("nat", "Natalie Portman");
			System.out.println("Data Has Been Added");
		}
		else {
			System.out.println("Data Retrived:");
			for(String key : c.keySet()) {
				System.out.println(key +": " + c.get(key));
			}
		}
		manager.stop();		
	}
	
}
