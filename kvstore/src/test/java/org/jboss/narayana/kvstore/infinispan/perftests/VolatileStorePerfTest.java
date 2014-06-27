package org.jboss.narayana.kvstore.infinispan.perftests;

public class VolatileStorePerfTest extends ObjectStorePerfTester {

	@Override
	public void setup() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.VolatileStore");
		
		setMessage("Volatile Store (Java Native)");
	}
}
