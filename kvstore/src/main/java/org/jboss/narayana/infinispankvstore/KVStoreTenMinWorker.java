package org.jboss.narayana.infinispankvstore;

import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import java.math.BigInteger;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.jboss.narayana.kvstore.XAResourceImpl;

public class KVStoreTenMinWorker implements Worker<BigInteger> {

	private final long TEN_MINS = 60000;

	private TransactionManager tm;
	private long initTimeMillis = -1;
	private long finiTimeMillis = -1;
	private long workTimeMillis = -1;

	public KVStoreTenMinWorker(TransactionManager tm) {
		this.tm = tm;
	}

	@Override
	public BigInteger doWork(BigInteger context, int niters, Result<BigInteger> opts) {

		long finishTime = System.currentTimeMillis() + TEN_MINS;

		while (System.currentTimeMillis() < finishTime) {

			try {
				tm.begin();

				XAResource xaResource1 = new XAResourceImpl();
				XAResource xaResource2 = new XAResourceImpl();

				tm.getTransaction().enlistResource(xaResource1);
				tm.getTransaction().enlistResource(xaResource2);

				tm.commit();
			} catch (Exception e) {
				throw new RuntimeException("ugh", e);
			}
		}

		return null;

	}

	@Override
	public void init() {
		initTimeMillis = System.currentTimeMillis();
	}

	@Override
	public void fini() {
		finiTimeMillis = System.currentTimeMillis();
	}

	public long getInitTimemillis() {
		return initTimeMillis;
	}

	public long getWorkTimeMillis() {
		return workTimeMillis;
	}

	public long getFiniTimeMillis() {
		return finiTimeMillis;
	}

}
