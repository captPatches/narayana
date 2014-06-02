package org.jboss.narayana.kvstore.infinispan.perftests;

public class DistributedStorePerfTest extends MillTester {

	@Override
	public void setup() {
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		if(runOnMill()) {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillDistCacheStore");
			
			setMessage("(Mill) Distributed Cache Store");
		} else {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.DistirbutedStore");
			setMessage("Distibuted Cache Store");
		}

	}

}
