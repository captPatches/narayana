package org.jboss.narayana.kvstore.infinispan.learning;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import org.junit.Test;

public class NoClusterViewTest {

	
	@Test
	public void withoutTester() {
		PerformanceTester<Integer> tester = new PerformanceTester<Integer>();
		Worker<Integer> worker = new NoSizeCheckWorker();
				
		Result opts = new Result<Integer>(20, 10000);
		tester.measureThroughput(worker, opts);
		
		if(opts.getErrorCount()>0)
			throw new RuntimeException("It went pear shaped");
		
		System.out.printf("\nRESULTS without cluster size check: totalTime %d\n", opts.getTotalMillis());
		
	}
}
