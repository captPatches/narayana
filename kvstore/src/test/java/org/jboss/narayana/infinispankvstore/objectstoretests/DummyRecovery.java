package org.jboss.narayana.infinispankvstore.objectstoretests;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class DummyRecovery extends RecoverySetup {

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            if (args[0].equals("-f")) {
                BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class).setObjectStoreDir(Util.recoveryStoreDir);
                new DummyRecovery().enlistmentFailure();
            } else if (args[0].equals("-r")) {
                startRecovery();
                new DummyRecovery().waitForRecovery();
                stopRecovery();
            }
        } else {
            System.err.println("to generate something to recover: java DummyRecovery -f");
            System.err.println("to recover after failure: java DummyRecovery -r");
        }
    }

    public void enlistmentFailure() throws NotSupportedException, RollbackException, SystemException, HeuristicMixedException, HeuristicRollbackException {
        if (Util.countLogRecords() != 0)
            return;

        // obtain a reference to the transaction manager
        TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

        // start a transaction
        tm.begin();

        // enlist some resources
        tm.getTransaction().enlistResource(new DummyXAResource(DummyXAResource.faultType.NONE));
        tm.getTransaction().enlistResource(new DummyXAResource(DummyXAResource.faultType.HALT));

        // commit any transactional work that was done on the two dummy XA resources
        System.out.println("Halting VM - next test run will not halt and should pass since there will be transactions to recover");

        tm.commit();
    }

    public void waitForRecovery() throws InterruptedException {
        int commitRequests = DummyXAResource.getCommitRequests();
        recoveryManager.scan();
        if(commitRequests < DummyXAResource.getCommitRequests()) System.out.println("MOO!!");
        if (commitRequests >= DummyXAResource.getCommitRequests())
            throw new RuntimeException("Did you forget to generate a recovery record before testing recovery (use the -f argument)");

        Util.emptyObjectStore();
    }
}
