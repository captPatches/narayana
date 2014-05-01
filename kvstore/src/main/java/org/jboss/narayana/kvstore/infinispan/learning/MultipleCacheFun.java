package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Testing the use (and hoepfully stopping of a multicached application
 * @author patches
 *
 */
public class MultipleCacheFun {

	private static DummyDataProvider data = new DummyDataProvider();
	
	public static void main(String[] args) throws Exception {

		
		EmbeddedCacheManager m1 = new DefaultCacheManager("multi-cache-cfg.xml");
			
		Cache<String, String> c1 = m1.getCache("replication-cache");
		Cache<String, String> c2 = m1 .getCache("distributed-cache");

		for(int i=0;i<data.length();i++) {
			c1.put(data.gotKey(i), data.gotValue(i));
			c2.put(data.boatKey(i), data.boatValue(i));
		}		
	}

}
