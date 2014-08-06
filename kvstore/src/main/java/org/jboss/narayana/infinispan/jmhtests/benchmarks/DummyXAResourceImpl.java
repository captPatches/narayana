package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class DummyXAResourceImpl implements XAResource {

	private Xid currentXid;
	private static int txTimeout;
	
	@Override
	public void commit(Xid xid, boolean arg1) throws XAException {
		
		if(!xid.equals(currentXid)) {
            System.out.println("XAResourceImpl.commit - wrong Xid!");
        }

        currentXid = null;
		
	}

	@Override
	public void end(Xid arg0, int arg1) throws XAException {
		
		//System.out.println("Made: " + count + " Commits");
		
	}

	@Override
	public void forget(Xid xid) throws XAException {
		 if(!xid.equals(currentXid)) {
	            System.out.println("XAResourceImpl.forget - wrong Xid!");
	        }
	        currentXid = null;
		
	}

	@Override
	public int getTransactionTimeout() throws XAException {
		System.out.println("XAResourceImpl.getTransactionTimeout() [returning "+txTimeout+"]");
        return txTimeout;
	}

	@Override
	public boolean isSameRM(XAResource arg0) throws XAException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int prepare(Xid arg0) throws XAException {
		// TODO Auto-generated method stub
		return XAResource.XA_OK;
	}

	@Override
	public Xid[] recover(int i) throws XAException {
		System.out.println("XAResourceImpl.recover(i="+i+")");
        return new Xid[0];
	}

	@Override
	public void rollback(Xid xid) throws XAException {
		System.out.println("DummyXAResourceImpl.rollback(Xid="+xid+")");
        if(!xid.equals(currentXid)) {
            System.out.println("XAResourceImpl.rollback - wrong Xid!");
        }
        throw new XAException("Transactions should not roll back");
	}

	@Override
	public boolean setTransactionTimeout(int i) throws XAException {
		txTimeout = i;
		return true;
	}

	@Override
	public void start(Xid xid, int i) throws XAException {
		if(currentXid != null) {
            System.out.println("XAResourceImpl.start - wrong Xid!");
        }
        currentXid = xid;
	}

}
