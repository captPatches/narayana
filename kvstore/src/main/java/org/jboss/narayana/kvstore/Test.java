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

import com.arjuna.ats.arjuna.ObjectModel;
import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
//import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
//import com.arjuna.ats.arjuna.common.Uid;
//import com.arjuna.ats.arjuna.common.arjPropertyManager;
//import com.arjuna.ats.arjuna.coordinator.BasicAction;
//import com.arjuna.ats.arjuna.coordinator.TwoPhaseCoordinator;
//import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
//import com.arjuna.ats.arjuna.objectstore.StoreManager;
//import com.arjuna.ats.arjuna.state.OutputObjectState;
//import com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqJournalEnvironmentBean;
import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqJournalEnvironmentBean;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
import org.hornetq.core.journal.impl.AIOSequentialFileFactory;
//import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
//import org.hornetq.core.journal.impl.AIOSequentialFileFactory;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic multi-threaded performance stress test harness for the ObjectStore.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2010-03
 */
public class Test
{
    public static void main(String[] args) throws Exception {

        setup416();



        // -Xss128k (java6), -Xss256k (java7)
        testLoop(1000000, 100);

        System.out.println("between runs...");
        Thread.sleep(10000);

        testLoop(1000000, 100);

        StoreManager.shutdown();
    }

    private static void setup416() {

//                arjPropertyManager.getCoordinatorEnvironmentBean().setCommitOnePhase(false);

        /*
            BeanPopulator.getDefaultInstance(HornetqJournalEnvironmentBean.class)
                .setStoreDir("/tmp/ostore");
//        BeanPopulator.getDefaultInstance(HornetqJournalEnvironmentBean.class).setLogRates(true);
            BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class)
                    .setObjectStoreType("com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqObjectStoreAdaptor");
        System.out.println("AIO: "+ AIOSequentialFileFactory.isSupported());
        */


/*
        if(AIOSequentialFileFactory.isSupported()) {
            System.out.println("set max io");
            BeanPopulator.getDefaultInstance(HornetqJournalEnvironmentBean.class).setMaxIO(500);
        }

// LD_LIBRARY_PATH=/home/jhalli/IdeaProjects/jboss/hornetq_trunk/native/bin
*/


//        BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class)
//                    .setObjectStoreType("com.arjuna.ats.internal.arjuna.objectstore.VolatileStore");

        System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.jbossts.MemcachedKVStoreT"); // TODO install to classpath


/*
//        BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class)
//                    .setObjectStoreType("com.arjuna.ats.internal.arjuna.objectstore.ShadowNoFileLockStore"); // the default
//        BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class)
//                    .setObjectStoreDir("/tmp/ostore/snfls");

        //arjPropertyManager.getCoordinatorEnvironmentBean().setActionStore("com.arjuna.ats.internal.arjuna.objectstore.LogStore");

        StoreManager.getParticipantStore(); // force init.
*/

        //TxControl.asyncPrepare = true;


    }

    private static void testLoop(int iterations, int threads) throws Exception {

        int NUM_TX = iterations;
        int BATCH_SIZE = 100;
        AtomicInteger count = new AtomicInteger(NUM_TX/BATCH_SIZE);
        final int nThreads = threads;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(nThreads +1); // workers + self
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i = 0; i < nThreads; i++) {
            executorService.execute(new TestWorkerTask(cyclicBarrier, count, BATCH_SIZE));
        }

        System.out.println(new Date());
        long start = System.nanoTime();

        cyclicBarrier.await();
        cyclicBarrier.await();

        long end = System.nanoTime();
        System.out.println(new Date());

        long duration_ms = (end - start) / 1000000L;

        System.out.println("  total time (ms): "+duration_ms);
        System.out.println("average time (ms): "+(1.0*duration_ms)/NUM_TX);
        System.out.println("tx / second: "+(1000.0/((1.0*duration_ms)/NUM_TX)));
        System.out.println(""+nThreads+" threads");
        System.out.println(""+XAResourceImpl.networkCalls.get()/NUM_TX+" network calls per tx");

        long actual_net_ms = XAResourceImpl.accumulatedSleepTimeActualNanos.get() / 1000000L;
        System.out.println(""+XAResourceImpl.accumulatedSleepTimeTargetMillis .get()+" target ms sleep");
        System.out.println(""+actual_net_ms+" actual ms sleep");

        long lockMillis = XAResourceImpl.accumulatedLockNanosA.get() / 1000000L;
        System.out.println(""+lockMillis+" lock millis");
        System.out.println(""+lockMillis/NUM_TX+" lock millis/tx");

        lockMillis = XAResourceImpl.accumulatedLockNanosB.get() / 1000000L;
        System.out.println(""+lockMillis+" lock millis");
        System.out.println(""+lockMillis/NUM_TX+" lock millis/tx");


        //Thread.Actual
        // sleep(Long.MAX_VALUE);

        executorService.shutdown();
    }
}

class TestWorkerTask implements Runnable {

    CyclicBarrier cyclicBarrier;
    AtomicInteger count;
    int batch_size = 0;

    TestWorkerTask(CyclicBarrier cyclicBarrier, AtomicInteger count, int batch_size) {
        this.cyclicBarrier = cyclicBarrier;
        this.count = count;
        this.batch_size = batch_size;
    }

    private void doTx(TransactionManager tm) throws Exception {
        tm.begin();
        Transaction t = tm.getTransaction();

        XAResource xaResource1 = new XAResourceImpl(false);
        t.enlistResource(xaResource1);

        XAResource xaResource2 = new XAResourceImpl(true);
        t.enlistResource(xaResource2);

        tm.commit();
    }

    public void run() {
        try {

            int x = 0;

            TransactionManager tm = new TransactionManagerImple();
            cyclicBarrier.await();

            while(count.decrementAndGet() >= 0) {

                for(int i = 0; i < batch_size; i++) {

                    doTx(tm);
                    x++;
                }

            }
            //System.out.println("done working "+x);
            cyclicBarrier.await();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}