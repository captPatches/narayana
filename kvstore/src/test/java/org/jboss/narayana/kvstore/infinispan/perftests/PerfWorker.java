package org.jboss.narayana.kvstore.infinispan.perftests;

import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import javax.transaction.TransactionManager;

public class PerfWorker implements Worker<Void> {

	private final TransactionManager tm;
	
	public PerfWorker(TransactionManager tm) {
		this.tm = tm;
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void fini() {
		
	}

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

		return context;
	}

}
