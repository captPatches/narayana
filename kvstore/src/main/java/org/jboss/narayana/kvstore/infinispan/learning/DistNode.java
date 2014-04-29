package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class DistNode {

	private static CrazyMadListener listener;
	
	public static void main(String[] args) {
		try {
		EmbeddedCacheManager manager = new DefaultCacheManager(
				"infinispan-dist-cfg.xml");
		
		listener = new CrazyMadListener(); //manager);
		
		manager.addListener(listener);
		
		manager.getCache("distributed-cache").addListener(listener);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
