package org.jboss.narayana.kvstore.infinispan;

/**
 * Implementation Of InfinispanKVStoreAbstract that uses a distributed cache
 * 
 * @author patches
 *
 */
public class DistributedStore extends InfinispanKVStoreAbstract {

	private final static String CONFIG_FILE = "generic-test-cfg.xml";
	private final static String CACHE_NAME = "dis";

	public DistributedStore() {
		super(CACHE_NAME, CONFIG_FILE);
	}
	
}
