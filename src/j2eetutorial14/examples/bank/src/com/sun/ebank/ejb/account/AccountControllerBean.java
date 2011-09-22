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
import java.rmi.RemoteException;
import com.sun.ebank.ejb.customer.LocalCustomerHome;
import com.sun.ebank.ejb.customer.LocalCustomer;
import com.sun.ebank.ejb.exception.*;
import com.sun.ebank.ejb.util.LocalNextId;
import com.sun.ebank.ejb.util.LocalNextIdHome;
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.EJBGetter;
import com.sun.ebank.util.AccountDetails;
import com.sun.ebank.util.CodedNames;


public class AccountControllerBean implements SessionBean {
    private String accountId;
    private LocalAccountHome accountHome;
    private LocalCustomerHome customerHome;
    private LocalNextIdHome nextIdHome;

    public AccountControllerBean() {
    }

    // account creation and removal methods
    public String createAccount(AccountDetails details, String customerId)
        throws IllegalAccountTypeException, CustomerNotFoundException, 
            InvalidParameterException {
        // makes a new account and enters it into db,
        LocalAccount account = null;
        LocalCustomer customer = null;
        LocalNextId nextId = null;

        Debug.print("AccountControllerBean createAccount");

        if (details.getType() == null) {
            throw new InvalidParameterException("null type");
        } else if (details.getDescription() == null) {
            throw new InvalidParameterException("null description");
        } else if (details.getBeginBalanceTimeStamp() == null) {
            throw new InvalidParameterException("null beginBalanceTimeStamp");
        } else if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        try {
            customer = customerHome.findByPrimaryKey(customerId);
        } catch (FinderException ex) {
            throw new CustomerNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        try {
            nextId = nextIdHome.findByPrimaryKey("account");
            account = accountHome.create(nextId.getNextId(), details.getType(),
                    details.getDescription(), details.getBalance(),
                    details.getCreditLine(), details.getBeginBalance(),
                    details.getBeginBalanceTimeStamp());
            account.addCustomer(customer);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return account.getAccountId();
    }

    public void removeAccount(String accountId)
        throws InvalidParameterException, AccountNotFoundException {
        // removes account
        LocalAccount account = null;

        Debug.print("AccountControllerBean removeAccount");

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            account = accountHome.findByPrimaryKey(accountId);
            account.remove();
        } catch (FinderException ex) {
            throw new AccountNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    // customer-account relationship methods
    public void addCustomerToAccount(String customerId, String accountId)
        throws InvalidParameterException, CustomerNotFoundException, 
            AccountNotFoundException {
        // adds another customer to the account
        LocalCustomer customer = null;
        LocalAccount account = null;

        Debug.print("AccountControllerBean addCustomerToAccount");

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        } else if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            Debug.print("AccountControllerBean Getting the account: " +
                accountId);
            account = accountHome.findByPrimaryKey(accountId);
        } catch (FinderException ex) {
            throw new AccountNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        try {
            Debug.print("AccountControllerBean Getting the customer: " +
                customerId);
            customer = customerHome.findByPrimaryKey(customerId);

            Debug.print(
                "AccountControllerBean Adding the customer to the account.");
            account.addCustomer(customer);
        } catch (FinderException ex) {
            throw new CustomerNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public void removeCustomerFromAccount(String customerId, String accountId)
        throws InvalidParameterException, CustomerNotFoundException, 
            AccountNotFoundException {
        // removes a customer from this account, but
        // the customer is not removed from the db
        LocalAccount account = null;
        LocalCustomer customer = null;

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        } else if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        Debug.print("AccountControllerBean removeCustomerFromAccount");

        try {
            account = accountHome.findByPrimaryKey(accountId);
        } catch (FinderException ex) {
            throw new AccountNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        try {
            customer = customerHome.findByPrimaryKey(customerId);
            account.removeCustomer(customer);
        } catch (FinderException ex) {
            throw new CustomerNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    // getters
    public ArrayList getAccountsOfCustomer(String customerId)
        throws InvalidParameterException, CustomerNotFoundException {
        // returns an ArrayList of AccountDetails
        // that correspond to the accounts for the specified
        // customer
        Debug.print("AccountControllerBean getAccountsOfCustomer");

        Collection accounts = null;
        LocalCustomer customer = null;

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        try {
            customer = customerHome.findByPrimaryKey(customerId);
            accounts = customer.getAccounts();
        } catch (FinderException ex) {
            throw new CustomerNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyAccountsToDetails(accounts);
    }

    public ArrayList getCustomerIds(String accountId)
        throws InvalidParameterException, AccountNotFoundException {
        Debug.print("AccountControllerBean getCustomerIds");

        Collection customers = null;
        LocalAccount account = null;

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            account = accountHome.findByPrimaryKey(accountId);
            customers = account.getCustomers();
        } catch (FinderException ex) {
            throw new AccountNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyCustomerIdsToArrayList(customers);
    }

    public AccountDetails getDetails(String accountId)
        throws InvalidParameterException, AccountNotFoundException {
        Debug.print("AccountControllerBean getDetails");

        AccountDetails details = null;
        LocalAccount account = null;

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            account = accountHome.findByPrimaryKey(accountId);
            details = new AccountDetails(accountId, account.getType(),
                    account.getDescription(), account.getBalance(),
                    account.getCreditLine(), account.getBeginBalance(),
                    account.getBeginBalanceTimeStamp());
        } catch (FinderException ex) {
            throw new AccountNotFoundException();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return details;
    }

    // ejb methods
    public void ejbCreate() {
        Debug.print("AccountControllerBean ejbCreate");

        try {
            customerHome = EJBGetter.getCustomerHome();
            accountHome = EJBGetter.getAccountHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (Exception ex) {
            throw new EJBException("ejbCreate: " + ex.getMessage());
        }
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
        Debug.print("AccountControllerBean ejbActivate");

        try {
            accountHome = EJBGetter.getAccountHome();
            customerHome = EJBGetter.getCustomerHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (Exception ex) {
            throw new EJBException("ejbActivate: " + ex.getMessage());
        }
    }

    public void ejbPassivate() {
        Debug.print("AccountControllerBean ejbPassiavte");
        accountHome = null;
        customerHome = null;
        nextIdHome = null;
    }

    public void setSessionContext(SessionContext sc) {
    }

    // private methods
    private ArrayList copyAccountsToDetails(Collection accounts) {
        ArrayList detailsList = new ArrayList();
        Iterator i = accounts.iterator();

        while (i.hasNext()) {
            LocalAccount account = (LocalAccount) i.next();
            AccountDetails details =
                new AccountDetails(account.getAccountId(), account.getType(),
                    account.getDescription(), account.getBalance(),
                    account.getCreditLine(), account.getBeginBalance(),
                    account.getBeginBalanceTimeStamp());
            detailsList.add(details);
        }

        return detailsList;
    }

    private ArrayList copyCustomerIdsToArrayList(Collection customers) {
        ArrayList customerIdList = new ArrayList();
        Iterator i = customers.iterator();

        while (i.hasNext()) {
            LocalCustomer customer = (LocalCustomer) i.next();
            customerIdList.add(customer.getCustomerId());
        }

        return customerIdList;
    }
}
