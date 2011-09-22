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

import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import com.sun.ebank.util.TxDetails;
import com.sun.ebank.ejb.exception.*;


public interface TxController extends EJBObject {
    // getters
    public ArrayList getTxsOfAccount(Date startDate, Date endDate,
        String accountId) throws RemoteException, InvalidParameterException;

    // returns an ArrayList of TxDetails objects
    // that correspond to the txs for the specified
    // account
    public TxDetails getDetails(String txId)
        throws RemoteException, TxNotFoundException, InvalidParameterException;

    // returns the TxDetails for the specified tx

    // business transaction methods
    public void withdraw(BigDecimal amount, String description, String accountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException, IllegalAccountTypeException, 
            InsufficientFundsException;

    // withdraws funds from a non-credit account
    public void deposit(BigDecimal amount, String description, String accountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException, IllegalAccountTypeException;

    // deposits funds to a non-credit account
    public void transferFunds(BigDecimal amount, String description,
        String fromAccountId, String toAccountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException, InsufficientFundsException, 
            InsufficientCreditException;

    // transfers funds from one account to another
    public void makeCharge(BigDecimal amount, String description,
        String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException, InsufficientCreditException, 
            RemoteException;

    // makes a charge to a credit account
    public void makePayment(BigDecimal amount, String description,
        String accountId)
        throws InvalidParameterException, AccountNotFoundException, 
            IllegalAccountTypeException, RemoteException;

    // makes a payment to a credit account
} // TxController
