package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class UsingDistributedCache {

	private static String[] keys = { "ned", "jamie", "jon", "Bran" };
	private static String[] vals = { "Ned Stark", "Jamie Lannister",
			"Jon Snow", "Bran Stark" };

	public static void main(String[] args) throws Exception {

		EmbeddedCacheManager manager = new DefaultCacheManager(
				"infinispan-dist-cfg.xml");
		Cache<String, String> c = manager.getCache("distributed-cache");

		if (c.isEmpty()) {
			for (int i = 0; i < keys.length; i++) {
				c.put(keys[i], vals[i]);
				System.out.println("Added " + vals[i]);
			}
		} else {
			for (String key : c.keySet()) {
				System.out.println(c.get(key));
			}
		}
		

	}

}
