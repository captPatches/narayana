package org.jboss.narayana.kvstore.infinispan;


public final class ReplicatedStore extends InfinispanKVStoreAbstract {

	private final static String CONFIG_FILE = "generic-test-cfg.xml";
	private final static String CACHE_NAME = "rep";

	public ReplicatedStore() {
		super( CACHE_NAME, CONFIG_FILE);
	}
}
