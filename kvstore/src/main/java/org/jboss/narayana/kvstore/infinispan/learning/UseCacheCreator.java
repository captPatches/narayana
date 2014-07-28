package org.jboss.narayana.kvstore.infinispan.learning;

public class UseCacheCreator {

	public static void main(String[]args) throws Exception {
		
		CacheCreator cache = new CacheCreator();
		cache.put("ned", "stark");
		System.out.println(cache.get("ned"));
	}
	
}
