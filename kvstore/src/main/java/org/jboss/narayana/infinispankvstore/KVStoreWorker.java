package org.jboss.narayana.infinispankvstore;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.XAResource;

import org.jboss.narayana.kvstore.XAResourceImpl;

import com.arjuna.ats.arjuna.objectstore.StoreManager;
import javax.transaction.TransactionManager;

import io.narayana.perf.*;

public class KVStoreWorker implements Worker {
	
	private long initTimeMillis = -1;
	private long finiTimeMillis = -1;
	private long workTimeMillis = -1;
	
	private String storeType = "infinispankvstore.HotrodRemoteKVStore";	
	
	/**
	 * Provide a constructor that set's the storeType
	 * May depreciate #setStoreType.
	 * @param storeType
	 */
	public KVStoreWorker(StoreType storeType) {
		
		setStoreType(storeType);
	}
	
	@Override
	public void doWork() {
		
		TransactionManager tm = getXtsManager();
		
		tm.begin();

        XAResource xaResource1 = new XAResourceImpl(true);
        XAResource xaResource2 = new XAResourceImpl(false);

        tm.getTransaction().enlistResource(xaResource1);
        tm.getTransaction().enlistResource(xaResource2);

        tm.commit();

        StoreManager.shutdown();		
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
    
    public void setStoreType(StoreType storeType) {
    	
    	this.storeType = storeType.getType();
    }

    private TransactionManager getXtsManager() {
    	  System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                  "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

          System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                  "org.jboss.narayana." + storeType);
          
          return (TransactionManager) com.arjuna.ats.jta.TransactionManager.transactionManager();
    }
    
}
