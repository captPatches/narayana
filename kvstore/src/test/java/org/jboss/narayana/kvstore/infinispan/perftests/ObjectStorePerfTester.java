package org.jboss.narayana.kvstore.infinispan.perftests;

import io.narayana.perf.Result;
import io.narayana.perf.WorkerWorkload;

import javax.transaction.TransactionManager;

import org.jboss.narayana.kvstore.XAResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public abstract class ObjectStorePerfTester {

	private String message = "Default Message";
	private final int transCount = 500000000;
	private final int threadsNum = 400;
	private final int batchSize = 10;
	
	@Before
	public void chooseIPStack() {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	@Test
	public void perTest() {

		// This sets the thread pool to the number of requested threads
		// automatically
		Result<Void> measurement = new Result<Void>(threadsNum, transCount, batchSize).measure( new WorkerWorkload<Void>() {
					
					private TransactionManager tm = getTransManager();
					
					@Override
					public Void doWork(Void context, int batchSize, Result<Void> measure) {
										
						try {
								tm.begin();
							
								tm.getTransaction().enlistResource( new XAResourceImpl() );
								tm.getTransaction().enlistResource( new XAResourceImpl() );
							
								tm.commit();
							
							} catch (Exception e) {
								throw new RuntimeException("There was an error, test failed");
							}
						return context;
					}
				
				});

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
