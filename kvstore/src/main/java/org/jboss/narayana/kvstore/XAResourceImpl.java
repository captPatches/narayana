/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2010, 2014
 * @author JBoss, by Red Hat.
 */
package org.jboss.narayana.kvstore;

import com.arjuna.ats.arjuna.common.Uid;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dummy implementation of the XAResource interface for test purposes.
 *
 * Can sleep to simulate network rounds trips to the RM and keep track of perf stats in a limited, crude way. Good enough for preliminary work.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2010-03
 */
public class XAResourceImpl implements XAResource
{
    private int txTimeout;

    protected Xid currentXid;

    public static final AtomicLong networkCalls = new AtomicLong();
    public static final AtomicLong accumulatedSleepTimeTargetMillis = new AtomicLong();
    public static final AtomicLong accumulatedSleepTimeActualNanos = new AtomicLong();

    public static final AtomicLong accumulatedLockNanosA = new AtomicLong();
    public static final AtomicLong accumulatedLockNanosB = new AtomicLong();

    private long lockNanos;

    protected void networkCall(int millis) {
        networkCalls.incrementAndGet();
        if(millis == 0) {
            return;
        }
        try {
            long beforeNanos = System.nanoTime();
            Thread.sleep(millis);
            long afterNanos = System.nanoTime();
            long diffNanos = afterNanos - beforeNanos;
            accumulatedSleepTimeActualNanos.addAndGet(diffNanos);
        } catch(InterruptedException e) {

        }
        accumulatedSleepTimeTargetMillis.addAndGet(millis);
    }

    ///////////////////////////

    private final Uid uid = new Uid();

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XAResourceImpl that = (XAResourceImpl) o;

        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return uid != null ? uid.hashCode() : 0;
    }

    /////////////////////

    public void commit(Xid xid, boolean b) throws XAException
    {
    //    networkCall(0);

//        long nanos = System.nanoTime() - lockNanos;
//
//        if(isResourceA) {
//            accumulatedLockNanosA.addAndGet(nanos);
//        } else {
//            accumulatedLockNanosB.addAndGet(nanos);
 //       }

 //       System.out.println("XAResourceImpl.commit(Xid="+xid+", b="+b+")");
        if(!xid.equals(currentXid)) {
            System.out.println("XAResourceImpl.commit - wrong Xid!");
        }

        currentXid = null;
    }

    public void end(Xid xid, int i) throws XAException {
        networkCall(0);
        //System.out.println("XAResourceImpl.end(Xid="+xid+", b="+i+")");
    }

    public void forget(Xid xid) throws XAException {
        //System.out.println("XAResourceImpl.forget(Xid="+xid+")");
        if(!xid.equals(currentXid)) {
            System.out.println("XAResourceImpl.forget - wrong Xid!");
        }
        currentXid = null;
    }

    public int getTransactionTimeout() throws XAException {
        System.out.println("XAResourceImpl.getTransactionTimeout() [returning "+txTimeout+"]");
        return txTimeout;
    }

    public boolean isSameRM(XAResource xaResource) throws XAException {
        //System.out.println("XAResourceImpl.isSameRM(xaResource="+xaResource+")");
        return false;
    }

    public int prepare(Xid xid) throws XAException {

//        lockNanos = System.nanoTime();
        //networkCall(3);


        //System.out.println("XAResourceImpl.prepare(Xid="+xid+")");
        return XAResource.XA_OK;
    }

    public Xid[] recover(int i) throws XAException {
        System.out.println("XAResourceImpl.recover(i="+i+")");
        return new Xid[0];
    }

    public void rollback(Xid xid) throws XAException {
        System.out.println("XAResourceImpl.rollback(Xid="+xid+")");
        if(!xid.equals(currentXid)) {
            System.out.println("XAResourceImpl.rollback - wrong Xid!");
        }
        currentXid = null;
    }

    public boolean setTransactionTimeout(int i) throws XAException {
        //System.out.println("XAResourceImpl.setTransactionTimeout(i="+i+")");
        txTimeout= i;
        return true;
    }

    public void start(Xid xid, int i) throws XAException {
        networkCall(0);
        //System.out.println("XAResourceImpl.start(Xid="+xid+", i="+i+")");
        if(currentXid != null) {
            System.out.println("XAResourceImpl.start - wrong Xid!");
        }
        currentXid = xid;
    }

    public String toString() {
        return new String("XAResourceImple("+txTimeout+", "+currentXid+")");
    }


}
