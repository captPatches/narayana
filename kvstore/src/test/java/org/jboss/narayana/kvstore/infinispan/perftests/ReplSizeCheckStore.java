package org.jboss.narayana.kvstore.infinispan.perftests;

public class ReplSizeCheckStore extends MillTester {
	@Override
	public void setup() {	
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
	
		if(runOnMill()) {
			setMessage("(Mill) Replicated Cluster Size Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillReplSizeCheckStore"
					);
			
		}
		else {
			setMessage("Replciated Cluster Size Checking Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.ReplicatedSizeCheckStore");
		}
	}
}