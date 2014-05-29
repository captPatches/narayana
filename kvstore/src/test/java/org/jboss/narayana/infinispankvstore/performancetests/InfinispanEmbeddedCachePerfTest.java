package org.jboss.narayana.infinispankvstore;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class InfinispanEmbeddedCachePerfTest {

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
				"org.jboss.narayana.infinispankvstore.NoReplInfinispanKVStore");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

		threadsNum = TestControlBean.threadsNum();
		transCount = TestControlBean.transCount();
		
	//	System.out.println("\n\r" + startNodes() + "\n\r");
	}

	@Test
	public void speedTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		KVStoreWorkerTM worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		System.out
				.printf("\nRESULTS: Infinispan Embedded performance: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());
	}

	@After
	public void tearDown() {
		StoreManager.shutdown();
	}
	
	private boolean startNodes() {
		
		String command = "java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.NodeForTesting";
		String[] TestNodeUp = {
				"/bin/sh",
				"-c",
				"jps | grep NodeForTesting"
		};
		
		try {
			System.out.println("Starting Node \n\r");
			Runtime.getRuntime().exec(command);
			Process p = Runtime.getRuntime().exec(TestNodeUp);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(line);
				if(line != null) return true;
			}
			in.close();
			return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

}
