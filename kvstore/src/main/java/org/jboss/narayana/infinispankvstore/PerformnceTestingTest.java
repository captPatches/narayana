package org.jboss.narayana.infinispankvstore;

import org.junit.Test;

public class PerformnceTestingTest {

	PerformanceTester<> tester = new PerformanceTester<>();
	
	@Test
	public void performanceTest() {
		
		
		// INFINISPAN implies HotRod Client
		KVStoreWorker worker = new KVStoreWorker(StoreType.INFINISPAN);
		
		
	}

}
