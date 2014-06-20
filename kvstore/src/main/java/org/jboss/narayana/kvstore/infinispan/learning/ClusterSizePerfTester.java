package org.jboss.narayana.kvstore.infinispan.learning;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import org.junit.Before;
import org.junit.Test;

public class ClusterSizePerfTester {

	@Before
	public void setup() {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	@Test
	public void perfTest() {
		PerformanceTester<Integer> tester = new PerformanceTester<Integer>();
		Worker<Integer> worker = new ClusterSizeWorker();
		
		Result<Integer> opts = new Result<Integer>(20, 10000);
		tester.measureThroughput(worker, opts);
		
		if(opts.getErrorCount() > 0)
			throw new RuntimeException("There was an error!!");
		
		System.out.printf("\nRESULTS with cluster size check: totalTime %d\n", opts.getTotalMillis());
	}
	
}
