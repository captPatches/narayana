package org.jboss.narayana.infinispankvstore;

import java.math.BigInteger;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class PerformnceTestingTest {
	
	@Test
	public void performanceTest() {
	
		//Initial Check
		int threadNum = 20;
		int jobsCount = 100000;
		
		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		KVStoreWorker worker = new KVStoreWorker(StoreType.INFINISPAN);
		
		
		Result<BigInteger> opts = new Result<BigInteger>(threadNum, jobsCount);
		Result<BigInteger> res = tester.measureThroughput(worker, opts);
		
		System.out.printf("Test performance for %d!: %d Txs / second (total time: %d ms versus %d ms)%n",
                opts.getNumberOfCalls(), opts.getThroughput(), opts.getTotalMillis());
		
		// Move the store shutdown
		StoreManager.shutdown();
		
	}

}
