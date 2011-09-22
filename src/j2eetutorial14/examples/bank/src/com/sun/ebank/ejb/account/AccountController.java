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

import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import com.sun.ebank.util.AccountDetails;
import com.sun.ebank.ejb.exception.*;


public interface AccountController extends EJBObject {
    // account creation and removal methods
    public String createAccount(AccountDetails details, String customerId)
        throws RemoteException, IllegalAccountTypeException, 
            CustomerNotFoundException, InvalidParameterException;

    // makes a new account and enters it into db,
    // customer for customerId must exist 1st
    public void removeAccount(String accountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException;

    // removes account from db

    // customer-account relationship methods
    public void addCustomerToAccount(String customerId, String accountId)
        throws RemoteException, InvalidParameterException, 
            CustomerNotFoundException, AccountNotFoundException;

    // adds another customer to the account
    public void removeCustomerFromAccount(String customerId, String accountId)
        throws RemoteException, InvalidParameterException, 
            CustomerNotFoundException, AccountNotFoundException;

    // removes a customer from the account, but
    // the customer is not removed from the db
    public ArrayList getAccountsOfCustomer(String customerId)
        throws RemoteException, InvalidParameterException, 
            CustomerNotFoundException;

    // returns an ArrayList of AccountDetails objects
    // that correspond to the accounts for the specified
    // customer
    public ArrayList getCustomerIds(String accountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException;

    public AccountDetails getDetails(String accountId)
        throws RemoteException, InvalidParameterException, 
            AccountNotFoundException;
} // AccountController
