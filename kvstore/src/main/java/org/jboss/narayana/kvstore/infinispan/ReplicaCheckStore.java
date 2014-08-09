package org.jboss.narayana.kvstore.infinispan;

public class ReplicaCheckStore extends InfinispanKVStoreAbstract {

	private final static String cacheName = "dis";
	private final static String cfgFile = "generic-test-cfg.xml";
	private String prefix = getPrefix();

	public ReplicaCheckStore() {
		super(cacheName, cfgFile);
	}

	@Override
	public void add(long id, byte[] txData) throws Exception {

		do {
			super.add(id, txData);
		} while (getStore().getAdvancedCache().getDistributionManager()
				.locate(prefix + id).size() < 1);
	}
}
