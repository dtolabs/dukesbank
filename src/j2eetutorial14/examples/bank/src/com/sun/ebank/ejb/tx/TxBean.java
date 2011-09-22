/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


package com.sun.ebank.ejb.tx;

import java.util.*;
import java.math.*;
import javax.ejb.*;
import javax.naming.*;
import com.sun.ebank.ejb.account.LocalAccount;
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.TxDetails;
import com.sun.ebank.util.CodedNames;


public abstract class TxBean implements EntityBean {
    private EntityContext context;

    public abstract String getTxId();

    public abstract void setTxId(String id);

    public abstract java.util.Date getTimeStamp();

    public abstract void setTimeStamp(java.util.Date timeStamp);

    public abstract BigDecimal getAmount();

    public abstract void setAmount(BigDecimal amount);

    public abstract BigDecimal getBalance();

    public abstract void setBalance(BigDecimal balance);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    // Access methods for relationship fields
    public abstract LocalAccount getAccount();

    public abstract void setAccount(LocalAccount account);

    // ejb methods
    public String ejbCreate(String txId, LocalAccount account,
        java.util.Date timeStamp, BigDecimal amount, BigDecimal balance,
        String description) throws CreateException {
        Debug.print("TxBean ejbCreate");

        setTxId(txId);
        setTimeStamp(timeStamp);
        setAmount(amount);
        setBalance(balance);
        setDescription(description);

        return null;
    }

    public void ejbRemove() {
    }

    public void setEntityContext(EntityContext ctx) {
        Debug.print("TxBean setEntityContext");
        context = ctx;
    }

    public void unsetEntityContext() {
        Debug.print("TxBean unsetEntityContext");
        context = null;
    }

    public void ejbLoad() {
        Debug.print("TxBean ejbLoad");
    }

    public void ejbStore() {
        Debug.print("TxBean ejbStore");
    }

    public void ejbActivate() {
        Debug.print("TxBean ejbActivate");
    }

    public void ejbPassivate() {
        Debug.print("TxBean ejbPassivate");
    }

    public void ejbPostCreate(String txId, LocalAccount account,
        java.util.Date timeStamp, BigDecimal amount, BigDecimal balance,
        String description) {
        setAccount(account);
    }
}
