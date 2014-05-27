package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class MultilpeWriteToOneStore {

	public static void main(String[] args)throws Exception {

		EmbeddedCacheManager manager = new DefaultCacheManager("multi-cache-cfg.xml");
		Cache<Integer, String> c = manager.getCache(); 
		
		for(int i=0;i<1000;i++) {
			
			c.put(i, "Jamie_"+i);
		}
	
	}

}
