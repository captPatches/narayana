package org.jboss.narayana.kvstore.infinispan.mill;

import org.jboss.narayana.kvstore.infinispan.InfinispanKVStoreAbstract;

@Deprecated
public class MillDistCacheStore extends InfinispanKVStoreAbstract {
	
	private final static String CONFIG_FILE = "configlib/jgroups-tcp-mill002-cfg.xml";
	private final static String CACHE_NAME = "repl-cache";

	public MillDistCacheStore() {
		super(CACHE_NAME, CONFIG_FILE);
	}

}
