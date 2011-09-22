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


package com.sun.ebank.ejb.account;

import java.util.*;
import java.math.*;
import javax.ejb.*;
import javax.naming.*;
import com.sun.ebank.ejb.customer.LocalCustomer;
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.CodedNames;
import com.sun.ebank.util.AccountDetails;


public abstract class AccountBean implements EntityBean {
    private EntityContext context;

    // Access methods for persistent fields
    public abstract String getAccountId();

    public abstract void setAccountId(String accountId);

    public abstract String getType();

    public abstract void setType(String type);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract BigDecimal getBalance();

    public abstract void setBalance(BigDecimal balance);

    public abstract BigDecimal getCreditLine();

    public abstract void setCreditLine(BigDecimal creditLine);

    public abstract BigDecimal getBeginBalance();

    public abstract void setBeginBalance(BigDecimal beginBalance);

    public abstract java.util.Date getBeginBalanceTimeStamp();

    public abstract void setBeginBalanceTimeStamp(
        java.util.Date beginBalanceTimeStamp);

    // Access methods for relationship fields
    public abstract Collection getCustomers();

    public abstract void setCustomers(Collection customers);

    // business methods
    public void addCustomer(LocalCustomer customer) {
        Debug.print("AccountBean addCustomer");

        try {
            Collection customers = getCustomers();
            customers.add(customer);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public void removeCustomer(LocalCustomer customer) {
        Debug.print("AccountBean removeCustomer");

        try {
            Collection customers = getCustomers();
            customers.remove(customer);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    // ejb methods
    public String ejbCreate(String accountId, String type, String description,
        BigDecimal balance, BigDecimal creditLine, BigDecimal beginBalance,
        java.util.Date beginBalanceTimeStamp) throws CreateException {
        Debug.print("AccountBean ejbCreate");

        setAccountId(accountId);
        setType(type);
        setDescription(description);
        setBalance(balance);
        setCreditLine(creditLine);
        setBeginBalance(beginBalance);
        setBeginBalanceTimeStamp(beginBalanceTimeStamp);

        return null;
    }

    public void ejbRemove() {
        Debug.print("AccountBean ejbRemove");
    }

    public void setEntityContext(EntityContext context) {
        Debug.print("AccountBean setEntityContext");
        context = context;
    }

    public void unsetEntityContext() {
        Debug.print("AccountBean unsetEntityContext");
        context = null;
    }

    public void ejbLoad() {
        Debug.print("AccountBean ejbLoad");
    }

    public void ejbStore() {
        Debug.print("AccountBean ejbStore");
    }

    public void ejbActivate() {
        Debug.print("AccountBean ejbActivate");
    }

    public void ejbPassivate() {
        Debug.print("AccountBean ejbPassivate");
    }

    public void ejbPostCreate(String accountId, String type,
        String description, BigDecimal balance, BigDecimal creditLine,
        BigDecimal beginBalance, java.util.Date beginBalanceTimeStamp) {
    }
}
