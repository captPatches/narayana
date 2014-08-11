package org.jboss.narayana.kvstore.infinispan;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;

public class ReplicaCheckStore extends InfinispanKVStoreAbstract {

	private final static String cacheName = "dis";
	private final static String cfgFile = "generic-test-cfg.xml";
	private String prefix = getPrefix();

	public ReplicaCheckStore() {
		super(cacheName, cfgFile);
	}

	@Override
	public void add(long id, byte[] txData) throws Exception {
		
		int count = 0;
		do {
			super.add(id, txData);
			count++;
		} while (getStore().getAdvancedCache().getDistributionManager()
				.locate(prefix + id).size() < 3 && count < 10);
		
		if(count == 10) {
			super.delete(id);
			throw new ObjectStoreException("Not Enough Replicas Created");
		}
	}
}
