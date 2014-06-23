package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class DistCacheTooFewSegs {

	public static void main(String[] args) throws IOException {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		Cache<String, String> cache = new DefaultCacheManager("generic-test-cfg.xml").getCache("segTest");
		cache.addListener(new FullListener());
		
		cache.put("ned", "stark");
	}

}
