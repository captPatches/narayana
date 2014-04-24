package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class MultipleCacheFun {

	private static Cache<String, String> c;
	private static Cache<String, String> d;
	
	public static void main(String[]args) {
	
		c = new DefaultCacheManager().getCache();
		d = new DefaultCacheManager().getCache();
		
		System.out.println("Are they one and the same? " + c);
		
		
		
	}
	
}
