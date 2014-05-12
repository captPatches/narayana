package org.jboss.narayana.infinispankvstore;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import java.io.IOException;
import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class MillDistPerfTest {
	private TransactionManager tm;
	private int threadsNum;
	private int transCount;

	@Before
	public void setup() {

		// Set System properties to use infinispanKVStore
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.MillDistributedCache");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

		threadsNum = TestControlBean.threadsNum();
		transCount = TestControlBean.transCount();
		
		startNodes();
	}

	@Test
	public void speedTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		KVStoreWorkerTM worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		System.out
				.printf("\nRESULTS: Infinispan Distributed Mode: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());
	}

	@After
	public void tearDown() {
		stopNodes();
		StoreManager.shutdown();
	}
	
	private boolean startNodes() {
		
		String[] command = {
				"/bin/sh",
				"-c",
				"ssh -t b3048933@mill004.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode\"",
				"ssh -t b3048933@mill005.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode\"",
				"ssh -t b3048933@mill005.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode\"",
		};
		try {
		Runtime.getRuntime().exec(command);
		return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private boolean stopNodes() {
		
		String[] command = {
				"/bin/sh",
				"-c",
				"kill -9 $(jps | grep Node | cut -d' ' -f1)"
		};
		
		try {
			Runtime.getRuntime().exec(command);
			return true;
		} catch(IOException e) {
			return false;
		}
	}

}
