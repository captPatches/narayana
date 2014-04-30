package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanDataRetrevialAttempt {

	public static void main(String[] args) {
	
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-rep-cfg.xml");
			Cache<String, String> c = manager.getCache("cluster-cache");
			
			for(String s : c.keySet()) {
				System.out.println(c.get(s));
			}
			
			} catch (Exception e){
				e.printStackTrace();
			}
		
	}

}
