package org.jboss.narayana.kvstore.infinispan;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.jboss.narayana.infinispankvstore.KVStoreWorkerTM;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class InfinispanWithHotRodPerfTest {

	@Test
	public void speedTest() {
		
		int threadsNum = 20;
		int transCount = 5000000;
		
		// Set System properties to use infinispanKVStore
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.infinispankvstore.HotrodRemoteCacheKVStore");
	
        TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
        
        PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
        KVStoreWorkerTM worker = new KVStoreWorkerTM(tm);
		
		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);
		
		System.out.printf("Infinispan (hotrod) performance: %d Txs / second (total time: %d)",
														opts.getThroughput(), opts.getTotalMillis());
		
		// Move the store shutdown
		StoreManager.shutdown();
        
	
	}
	
}
