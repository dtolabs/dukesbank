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


public class CustomerBean {
    private BeanManager beanManager;
    private String customer;
    private String account;

    public CustomerBean() {
        beanManager = new BeanManager();
        customer = null;
        account = null;

        /*
           Debug.print("CustomerBean: Creating BeanManager.");
           beanManager = new BeanManager();
           Debug.print("CustomerBean: BeanManager created.");
         */
    }

    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public BeanManager getBeanManager() {
        return this.beanManager;
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
            ad = beanManager.getAccountController()
                            .getDetails(this.account);
        } catch (InvalidParameterException e) {
            Debug.print(e.getMessage());

            // Not possible
        } catch (RemoteException e) {
            Debug.print(e.getMessage());

            // Not possible
        } catch (AccountNotFoundException e) {
            Debug.print(e.getMessage());

            // Not possible
        }

        Debug.print(ad.getAccountId());

        return ad;
    }

    public ArrayList getAccounts() {
        ArrayList accounts = null;

        try {
            accounts = beanManager.getAccountController()
                                  .getAccountsOfCustomer(this.customer);
        } catch (InvalidParameterException e) {
            Debug.print(e.getMessage());

            // Not possible
        } catch (RemoteException e) {
            Debug.print(e.getMessage());

            // Not possible
        } catch (CustomerNotFoundException e) {
            Debug.print(e.getMessage());

            // Not possible
        }

        return accounts;
    }

    public void destroy() {
        beanManager = null;
        customer = null;
        account = null;
    }
}
