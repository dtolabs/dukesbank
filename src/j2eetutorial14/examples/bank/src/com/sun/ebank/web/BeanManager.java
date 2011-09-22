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


package com.sun.ebank.web;

import javax.ejb.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import com.sun.ebank.ejb.exception.*;
import com.sun.ebank.util.*;
import com.sun.ebank.ejb.account.*;
import com.sun.ebank.ejb.customer.*;
import com.sun.ebank.ejb.tx.*;
import java.util.*;


public class BeanManager {
    private CustomerController custctl;
    private AccountController acctctl;
    private TxController txctl;
    private String customer;
    private String account;

    public BeanManager() {
        if (custctl == null) {
            try {
                CustomerControllerHome home =
                    EJBGetter.getCustomerControllerHome();
                custctl = home.create();
            } catch (RemoteException ex) {
                Debug.print("Couldn't create customer bean." + ex.getMessage());
            } catch (CreateException ex) {
                Debug.print("Couldn't create customer bean." + ex.getMessage());
            } catch (NamingException ex) {
                Debug.print("Unable to lookup home: " +
                    CodedNames.CUSTOMER_CONTROLLER_EJBHOME + ex.getMessage());
            }
        }

        if (acctctl == null) {
            try {
                AccountControllerHome home =
                    EJBGetter.getAccountControllerHome();
                acctctl = home.create();
            } catch (RemoteException ex) {
                Debug.print("Couldn't create account bean." + ex.getMessage());
            } catch (CreateException ex) {
                Debug.print("Couldn't create account bean." + ex.getMessage());
            } catch (NamingException ex) {
                Debug.print("Unable to lookup home: " +
                    CodedNames.ACCOUNT_CONTROLLER_EJBHOME + ex.getMessage());
            }
        }

        if (txctl == null) {
            try {
                TxControllerHome home = EJBGetter.getTxControllerHome();
                txctl = home.create();
            } catch (RemoteException ex) {
                Debug.print("Couldn't create transaction bean." +
                    ex.getMessage());
            } catch (CreateException ex) {
                Debug.print("Couldn't create transaction bean." +
                    ex.getMessage());
            } catch (NamingException ex) {
                Debug.print("Unable to lookup home: " +
                    CodedNames.TX_CONTROLLER_EJBHOME + ex.getMessage());
            }
        }
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return this.customer;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return this.account;
    }

    public AccountDetails getAccountDetails() {
        AccountDetails ad = null;

        try {
            ad = acctctl.getDetails(this.account);
        } catch (InvalidParameterException ex) {
            Debug.print(ex.getMessage());
        } catch (AccountNotFoundException ex) {
            Debug.print(ex.getMessage());
        } catch (RemoteException e) {
            Debug.print(e.getMessage());
        }

        Debug.print(ad.getAccountId());

        return ad;
    }

    public ArrayList getAccounts() {
        ArrayList accounts = null;

        try {
            accounts = acctctl.getAccountsOfCustomer(this.customer);
        } catch (InvalidParameterException ex) {
            Debug.print(ex.getMessage());
        } catch (CustomerNotFoundException ex) {
            Debug.print(ex.getMessage());
        } catch (RemoteException e) {
            Debug.print(e.getMessage());

            // Not possible
        }

        return accounts;
    }

    public CustomerController getCustomerController() {
        return custctl;
    }

    public AccountController getAccountController() {
        return acctctl;
    }

    public TxController getTxController() {
        return txctl;
    }

    public void destroy() {
        custctl = null;
        acctctl = null;
        txctl = null;
    }
}
