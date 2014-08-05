package org.jboss.narayana.kvstore.infinispan;


public class DistributedStore extends InfinispanKVStoreAbstract {

	private final static String CONFIG_FILE = "generic-test-cfg.xml";
	private final static String CACHE_NAME = "dis";

	public DistributedStore() {
		super(CACHE_NAME, CONFIG_FILE);
	}
	
}
