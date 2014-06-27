package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Before;

public class PutWithClusterCheck {

	private Cache<String, String> cache;
	private EmbeddedCacheManager manager;
	
	@Before
	public void setup() throws Exception {
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		cache = manager.getCache("rep");
	}


}
