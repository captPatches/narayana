package org.jboss.narayana.kvstore.infinispan.perftests;

import static org.junit.Assert.assertEquals;
import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;
import io.narayana.perf.WorkerWorkload;

import java.math.BigInteger;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

// Deprecated test class is left in there for academic reasons.
@SuppressWarnings("deprecation")
public abstract class ObjectStorePerfTester {

	private TransactionManager tm = getTransManager();
	private String message = "Default Message";
	
	private final int transCount = 30000000;
	private final int threadsNum = 200;
	private int batchSize = 1;
	
	@BeforeClass
	public static void classSetup() {
		System.setProperty("java.net.preferIPv4Stack", "true");	 
	}


	/*
	 * Deprecated Methods left in here for academic reasons, when all problems have chased down,
	 * These will be removed.
	 */
	@Deprecated
	@Test
	@Ignore
	public void test() {

		// -1 uses the default batch size for testing
		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>(
				400, -1);
		Worker<BigInteger> worker = new TxWorker(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		// discount any tests that contain an error
		if (opts.getErrorCount() > 0)
			throw new RuntimeException("There was error - Test Failed!!");

		System.out.printf("\nRESULTS: " + message
				+ ": %d Txs / second (total time: %d)\n", opts.getThroughput(),
				opts.getTotalMillis());
	}

	@Test
	public void perfTest() {

		// This sets the thread pool to the number of requested threads
		// automatically
		Result<Void> measurement = new Result<Void>(threadsNum, transCount,
				batchSize).measure(new WorkerWorkload<Void>() {

			// int i=0;
			private TransactionManager tm = getTransManager();

			@Override
			public Void doWork(Void context, int batchSize, Result<Void> measure) {

				for(int i=0; i<batchSize; i++) {
					try {
						tm.begin();


						XAResource xa1 = new DummyXAResourceImpl();
						XAResource xa2 = new DummyXAResourceImpl();
						tm.getTransaction().enlistResource(xa1);
						tm.getTransaction().enlistResource(xa2);

						tm.commit();

					} catch (Exception e) {
						throw new RuntimeException(
								"There was an error, test failed");
					}
				}
				/*
				 * i++; if(i%1000 == 0) { System.out.println(i); }
				 */
				return context;
			}

		});

		assertEquals("There should be no errors!!", 0,
				measurement.getErrorCount());
		System.out.printf("\nRESULTS: " + message
				+ ": %d Txs / second (total time: %d)\n",
				measurement.getThroughput(), measurement.getTotalMillis());
		System.out.printf("For %d jobs", transCount);
	}

	@Test
	@Ignore
	public void seperateWorkerWorkloadTest() {

		
		System.out.println("txs to be run: " + transCount);
		// This sets the thread pool to the number of requested threads
		// automatically
		Result<Void> measurement = new Result<Void>(threadsNum, transCount, batchSize).measure( new PerfWorker(tm));
					
		assertEquals("There should be no errors!!", 0, measurement.getErrorCount());
		System.out.printf("\nRESULTS: " + message
				+ ": %d Txs / second (total time: %d)\n",
				measurement.getThroughput(), measurement.getTotalMillis());
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
