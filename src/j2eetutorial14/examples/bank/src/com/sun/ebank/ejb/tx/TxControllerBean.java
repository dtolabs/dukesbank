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
import java.rmi.RemoteException;
import com.sun.ebank.ejb.account.LocalAccountHome;
import com.sun.ebank.ejb.account.LocalAccount;
import com.sun.ebank.ejb.util.LocalNextId;
import com.sun.ebank.ejb.util.LocalNextIdHome;
import com.sun.ebank.ejb.exception.*;
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.TxDetails;
import com.sun.ebank.util.EJBGetter;
import com.sun.ebank.util.CodedNames;
import com.sun.ebank.util.DomainUtil;


public class TxControllerBean implements SessionBean {
    private LocalTxHome txHome;
    private LocalAccountHome accountHome;
    private LocalNextIdHome nextIdHome;
    private SessionContext context;
    private BigDecimal bigZero = new BigDecimal("0.00");

    public TxControllerBean() {
    }

    public ArrayList getTxsOfAccount(java.util.Date startDate,
        java.util.Date endDate, String accountId)
        throws InvalidParameterException {
        Debug.print("TxControllerBean  getTxsOfAccount");

        Collection txIds;
        ArrayList txList = new ArrayList();

        if (startDate == null) {
            throw new InvalidParameterException("null startDate");
        }

        if (endDate == null) {
            throw new InvalidParameterException("null endDate");
        }

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            txIds = txHome.findByAccountId(startDate, endDate, accountId);
        } catch (Exception ex) {
            return txList;
        }

        return copyTxsToDetails(txIds);
    } // getTxsOfAccount

    public TxDetails getDetails(String txId)
        throws TxNotFoundException, InvalidParameterException {
        Debug.print("TxControllerBean getDetails");

        TxDetails details;

        if (txId == null) {
            throw new InvalidParameterException("null txId");
        }

        try {
            LocalTx tx = txHome.findByPrimaryKey(txId);
            details = new TxDetails(tx.getTxId(), tx.getTimeStamp(),
                    tx.getAmount(), tx.getBalance(), tx.getDescription());
        } catch (Exception ex) {
            throw new TxNotFoundException(txId);
        }

        return details;
    } // getDetails

    public void withdraw(BigDecimal amount, String description, String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException, InsufficientFundsException {
        Debug.print("TxControllerBean withdraw");

        LocalAccount account = checkAccountArgs(amount, description, accountId);

        String type = account.getType();

        if (DomainUtil.isCreditAccount(type)) {
            context.setRollbackOnly();
            throw new IllegalAccountTypeException(type);
        }

        BigDecimal newBalance = account.getBalance()
                                       .subtract(amount);

        if (newBalance.compareTo(bigZero) == -1) {
            context.setRollbackOnly();
            throw new InsufficientFundsException();
        }

        executeTx(amount.negate(), description, newBalance, account);
    }

    public void deposit(BigDecimal amount, String description, String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException {
        Debug.print("TxControllerBean deposit");

        LocalAccount account = checkAccountArgs(amount, description, accountId);

        String type = account.getType();

        if (DomainUtil.isCreditAccount(type)) {
            context.setRollbackOnly();
            throw new IllegalAccountTypeException(type);
        }

        BigDecimal newBalance = account.getBalance()
                                       .add(amount);
        executeTx(amount, description, newBalance, account);
    }

    public void makeCharge(BigDecimal amount, String description,
        String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException, InsufficientCreditException {
        Debug.print("TxControllerBean charge");

        LocalAccount account = checkAccountArgs(amount, description, accountId);

        String type = account.getType();

        if (DomainUtil.isCreditAccount(type) == false) {
            context.setRollbackOnly();
            throw new IllegalAccountTypeException(type);
        }

        BigDecimal newBalance = account.getBalance()
                                       .add(amount);

        if (newBalance.compareTo(account.getCreditLine()) == 1) {
            context.setRollbackOnly();
            throw new InsufficientCreditException();
        }

        executeTx(amount, description, newBalance, account);
    }

    public void makePayment(BigDecimal amount, String description,
        String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException {
        Debug.print("TxControllerBean makePayment");

        LocalAccount account = checkAccountArgs(amount, description, accountId);

        String type = account.getType();

        if (DomainUtil.isCreditAccount(type) == false) {
            context.setRollbackOnly();
            throw new IllegalAccountTypeException(type);
        }

        BigDecimal newBalance = account.getBalance()
                                       .subtract(amount);
        executeTx(amount, description, newBalance, account);
    }

    public void transferFunds(BigDecimal amount, String description,
        String fromAccountId, String toAccountId)
        throws InvalidParameterException, AccountNotFoundException, 
            InsufficientFundsException, InsufficientCreditException {
        LocalAccount fromAccount =
            checkAccountArgs(amount, description, fromAccountId);
        LocalAccount toAccount =
            checkAccountArgs(amount, description, toAccountId);

        String fromType = fromAccount.getType();
        BigDecimal fromBalance = fromAccount.getBalance();

        if (DomainUtil.isCreditAccount(fromType)) {
            BigDecimal fromNewBalance = fromBalance.add(amount);

            if (fromNewBalance.compareTo(fromAccount.getCreditLine()) == 1) {
                context.setRollbackOnly();
                throw new InsufficientCreditException();
            }

            executeTx(amount, description, fromNewBalance, fromAccount);
        } else {
            BigDecimal fromNewBalance = fromBalance.subtract(amount);

            if (fromNewBalance.compareTo(bigZero) == -1) {
                context.setRollbackOnly();
                throw new InsufficientFundsException();
            }

            executeTx(amount.negate(), description, fromNewBalance, fromAccount);
        }

        String toType = toAccount.getType();
        BigDecimal toBalance = toAccount.getBalance();

        if (DomainUtil.isCreditAccount(toType)) {
            BigDecimal toNewBalance = toBalance.subtract(amount);
            executeTx(amount.negate(), description, toNewBalance, toAccount);
        } else {
            BigDecimal toNewBalance = toBalance.add(amount);
            executeTx(amount, description, toNewBalance, toAccount);
        }
    } // transferFunds

    // private methods
    private void executeTx(BigDecimal amount, String description,
        BigDecimal newBalance, LocalAccount account) {
        Debug.print("TxControllerBean executeTx");

        LocalTx tx = null;
        LocalNextId nextId = null;

        // Set the new balance and create a new transaction       
        try {
            account.setBalance(newBalance);
            nextId = nextIdHome.findByPrimaryKey("tx");
            tx = txHome.create(nextId.getNextId(), account,
                    new java.util.Date(), amount, newBalance, description);
        } catch (Exception ex) {
            throw new EJBException("executeTx: " + ex.getMessage());
        }
    }

    private LocalAccount checkAccountArgs(BigDecimal amount,
        String description, String accountId)
        throws InvalidParameterException, AccountNotFoundException {
        LocalAccount account = null;

        if (description == null) {
            throw new InvalidParameterException("null description");
        }

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        if (amount.compareTo(bigZero) != 1) {
            throw new InvalidParameterException("amount <= 0");
        }

        try {
            account = accountHome.findByPrimaryKey(accountId);
        } catch (Exception ex) {
            throw new AccountNotFoundException(accountId);
        }

        return account;
    } // checkAccountArgs

    private ArrayList copyTxsToDetails(Collection txs) {
        ArrayList detailsList = new ArrayList();
        Iterator i = txs.iterator();

        try {
            while (i.hasNext()) {
                LocalTx tx = (LocalTx) i.next();
                TxDetails txDetails =
                    new TxDetails(tx.getTxId(), tx.getTimeStamp(),
                        tx.getAmount(), tx.getBalance(), tx.getDescription());
                detailsList.add(txDetails);
            }
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return detailsList;
    }

    // ejb methods
    public void ejbCreate() {
        Debug.print("TxControllerBean ejbCreate");

        try {
            txHome = EJBGetter.getTxHome();
            accountHome = EJBGetter.getAccountHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (Exception ex) {
            throw new EJBException("ejbCreate: " + ex.getMessage());
        }
    } // ejbCreate

    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
        Debug.print("TxControllerBean ejbActivate");

        try {
            accountHome = EJBGetter.getAccountHome();
            txHome = EJBGetter.getTxHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (Exception ex) {
            throw new EJBException("ejbActivate: " + ex.getMessage());
        }
    }

    public void ejbPassivate() {
        Debug.print("TxControllerBean ejbPassiavte");
        accountHome = null;
        txHome = null;
        nextIdHome = null;
    }
}
