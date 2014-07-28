package org.jboss.narayana.kvstore.infinispan.learning;

public class CacheCreator extends AbstractCacheCreater {

	private final static String CACHE_NAME = "rep";
	private final static String CFG_FILE = "generic-test-cfg.xml";

	public CacheCreator() throws Exception {
		super(CFG_FILE, CACHE_NAME);
	}
}
