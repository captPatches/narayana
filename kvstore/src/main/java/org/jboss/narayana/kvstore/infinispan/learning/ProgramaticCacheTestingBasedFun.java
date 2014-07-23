package org.jboss.narayana.kvstore.infinispan.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Test;

public abstract class ProgramaticCacheTestingBasedFun {

	private DefaultCacheManager manager = getManager();
	private Cache<String, String> cache = getCache(manager);

	protected abstract DefaultCacheManager getManager();

	protected abstract Cache<String, String> getCache(DefaultCacheManager manager);

	@Test
	public void test() {
		assertNotNull("Manager Cannot be Null", manager);

		cache.put("ned", "stark");
		assertEquals("Cache should hold <stark> for key <ned>:", "stark",
				cache.get("ned"));
	}
}
