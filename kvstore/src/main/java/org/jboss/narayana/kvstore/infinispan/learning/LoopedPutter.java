package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.Random;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class LoopedPutter {

	public static void main(String[]args) throws Exception {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		DefaultCacheManager manager = new DefaultCacheManager("multi-cache-cfg.xml");
		Cache<Integer, Integer> c = manager.getCache("distributed-cache");
		Random rand = new Random();
		
		System.out.println(manager.getClusterSize());
		
		for(int i=0; i<600000000; i++) {
			c.put(rand.nextInt(),rand.nextInt());
			//if(i%10000 == 0) System.out.println(i);
		}
		
		manager.stop();
		System.out.println("Finished :-)");
	}
	
}
