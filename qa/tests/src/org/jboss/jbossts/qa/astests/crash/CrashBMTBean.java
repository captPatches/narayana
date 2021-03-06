/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
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
 * (C) 2008,
 * @author JBoss Inc.
 */
package org.jboss.jbossts.qa.astests.crash;

import org.jboss.jbossts.qa.astests.recovery.ASFailureSpec;
import org.jboss.jbossts.qa.astests.recovery.TestASRecovery;

import javax.ejb.SessionContext;
import javax.ejb.SessionBean;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class CrashBMTBean implements SessionBean {
    private SessionContext context;

    public void setSessionContext(SessionContext context) { this.context = context; }
    public void ejbCreate() { }
    public void ejbActivate() { }
    public void ejbPassivate() { }
    public void ejbRemove() { }

    public CrashBMTBean()
    {
    }

    public String testXA(String ... args)
    {
         return "Passed";
    }

    public String testXA(ASFailureSpec... specs)
    {
        TestASRecovery xatest = new TestASRecovery();
        String txStatus = getStatus(context.getUserTransaction());

        if (txStatus != null)
            System.out.println("BMT method called with tx status: " + txStatus);
        
        System.out.println("BMT testXA called with " + specs.length + " specs");

        for (ASFailureSpec spec : specs)
            xatest.addResource(spec);

        return xatest.startTest() ? "Passed" : "Failed";
    }

    public String getStatus(UserTransaction tx)
    {
        if (tx == null)
            return null;

        try
        {
            return org.jboss.tm.TxUtils.getStatusAsString(tx.getStatus());
/*
            switch (tx.getStatus())
            {
                case Status.STATUS_ACTIVE:
                    return "A transaction is associated with the target object and it is in the active state.";
                case Status.STATUS_COMMITTED:
                    return "A transaction is associated with the target object and it has been committed.";
                case Status.STATUS_COMMITTING:
                    return "A transaction is associated with the target object and it is in the process of committing.";
                case Status.STATUS_MARKED_ROLLBACK:
                    return "A transaction is associated with the target object and it has been marked for rollback, perhaps as a result of a setRollbackOnly operation.";
                case Status.STATUS_NO_TRANSACTION:
                    return "No transaction is currently associated with the target object.";
                case Status.STATUS_PREPARED:
                    return "A transaction is associated with the target object and it has been prepared.";
                case Status.STATUS_PREPARING:
                    return "A transaction is associated with the target object and it is in the process of preparing.";
                case Status.STATUS_ROLLEDBACK:
                    return "A transaction is associated with the target object and the outcome has been determined to be rollback.";
                case Status.STATUS_ROLLING_BACK:
                    return "A transaction is associated with the target object and it is in the process of rolling back.";
                case Status.STATUS_UNKNOWN:
                    return "A transaction is associated with the target object but its current status cannot be determined.";
                default:
                    return "Unknown tx status code: " + tx.getStatus();
            }
*/
        }
        catch (SystemException e)
        {
            return "tx status error: " + e.getMessage();
        }
    }
}
