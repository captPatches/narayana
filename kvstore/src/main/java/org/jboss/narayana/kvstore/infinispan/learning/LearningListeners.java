package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class LearningListeners {

	public static void main(String[] args) {

		Cache<String, String> c = new DefaultCacheManager().getCache();

		c.addListener(new SimpleListener());

		c.put("ned", "Eddard Stark");
		c.put("dany", "Danereys Targaryan");

	}

}
