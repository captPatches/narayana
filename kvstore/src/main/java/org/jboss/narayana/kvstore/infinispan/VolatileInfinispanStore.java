package org.jboss.narayana.kvstore.infinispan;

/**
 * Uses an infinispan cache rather than Hash map for the object store,
 * however will attempt no replication of data.  Useful to get a baseline
 * for performance tests.
 * 
 * @author patches
 *
 */
public class VolatileInfinispanStore extends InfinispanKVStoreAbstract {
	
	private final static String CACHE_NAME = "ImaginaryCache";
	private final static String CFG_FILE = "";
	
	public VolatileInfinispanStore() {
		super(CACHE_NAME, CFG_FILE);
	}
}
