package org.jboss.narayana.kvstore.infinispan.perftests;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.jboss.narayana.infinispankvstore.KVStoreWorkerTM;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public abstract class ObjectStorePerfTester {

	private String message = "Default Message";
	private final int transCount = 50000;
	private final int threadsNum = 250;
	private TransactionManager tm = getTransManager();
	
	@Before
	public void chooseIPStack() {
		System.setProperty("java.net.preferIPv4Stack","true");
	}
	
	@Test
	public void perTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		Worker<BigInteger> worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		// discount any tests that contain an error
		if (opts.getErrorCount() > 0)
			throw new RuntimeException("There was error - Test Failed!!");

		System.out
				.printf("\nRESULTS: " + message + ": %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());
	}
	
	@After
	public void teardown() {
		StoreManager.shutdown();
	}
	
	protected final void setMessage(String msg) {
		this.message = msg;
	}
	
	protected abstract TransactionManager getTransManager();

}
