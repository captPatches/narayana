package org.jboss.narayana.kvstore.infinispan.perftests;

import io.narayana.perf.Result;
import io.narayana.perf.WorkerWorkload;

import javax.transaction.TransactionManager;

public class PerfWorkerWorkload implements WorkerWorkload<Void> {

	private final TransactionManager tm;
	
	@Override
	public Void doWork(Void context, int batchSize, Result<Void> measurement) {
		
		try {
			tm.begin();
		
			tm.getTransaction().enlistResource( new DummyXAResourceImpl() );
			tm.getTransaction().enlistResource( new DummyXAResourceImpl() );
		
			tm.commit();
		
		} catch (Exception e) {
			throw new RuntimeException("There was an error, test failed");
		}

		return (Void) context;
	}
	
	public PerfWorkerWorkload(TransactionManager tm) {
		this.tm = tm;
	}

}
