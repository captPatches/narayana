package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanInsertCacheDataXMLAttempt {

	public static void main(String[] args) {

		try {
			EmbeddedCacheManager manager = new DefaultCacheManager(
					"infinispan-dist-cfg.xml");
			Cache<String, String> c = manager.getCache("distributed-cache");
	//		c.addListener(new CrazyMadListener());

			c.put("eva", "Eva Green");

			System.out.println(c.get("eva"));

			c.stop();
			 manager.stop();

			// System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
