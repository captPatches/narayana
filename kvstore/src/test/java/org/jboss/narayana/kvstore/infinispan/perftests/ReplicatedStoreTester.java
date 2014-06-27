package org.jboss.narayana.kvstore.infinispan.perftests;

public class ReplicatedStoreTester extends MillTester {

	@Override
	public void setup() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		if(runOnMill()) {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillReplCacheStore");
			
			setMessage("(Mill) Replicated Cache Store");
		} else {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.ReplicatedStore");
			setMessage("Replicated Cache Store");
		}
	}

}
