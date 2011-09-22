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


package com.sun.ebank.ejb.customer;

import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.RemoteException;
import com.sun.ebank.ejb.customer.LocalCustomer;
import com.sun.ebank.ejb.customer.LocalCustomerHome;
import com.sun.ebank.ejb.account.LocalAccount;
import com.sun.ebank.ejb.account.LocalAccountHome;
import com.sun.ebank.ejb.exception.InvalidParameterException;
import com.sun.ebank.ejb.exception.CustomerNotFoundException;
import com.sun.ebank.ejb.util.LocalNextId;
import com.sun.ebank.ejb.util.LocalNextIdHome;
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.CustomerDetails;
import com.sun.ebank.util.EJBGetter;
import com.sun.ebank.util.CodedNames;


public class CustomerControllerBean implements SessionBean {
    private String customerId = null;
    private LocalCustomerHome customerHome = null;
    private LocalAccountHome accountHome = null;
    private LocalNextIdHome nextIdHome;

    public CustomerControllerBean() {
    }

    // customer creation and removal methods
    public String createCustomer(CustomerDetails details)
        throws InvalidParameterException {
        // makes a new customer and enters it into db
        LocalCustomer customer = null;
        LocalNextId nextId = null;

        Debug.print("CustomerControllerBean createCustomer");

        if (details.getLastName() == null) {
            throw new InvalidParameterException("null lastName");
        }

        if (details.getFirstName() == null) {
            throw new InvalidParameterException("null firstName");
        }

        try {
            Debug.print("CustomerControllerBean creating nextId bean");
            nextId = nextIdHome.findByPrimaryKey("customer");
            Debug.print("Creating LocalCustomer with customerHome.create");
            customer = customerHome.create(nextId.getNextId(),
                    details.getLastName(), details.getFirstName(),
                    details.getMiddleInitial(), details.getStreet(),
                    details.getCity(), details.getState(), details.getZip(),
                    details.getPhone(), details.getEmail());
        } catch (Exception ex) {
            throw new EJBException("createCustomer: " + ex.getMessage());
        }

        return customer.getCustomerId();
    }

    public void removeCustomer(String customerId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException {
        // removes customer from db
        Debug.print("CustomerControllerBean removeCustomer");

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        try {
            LocalCustomer customer = customerHome.findByPrimaryKey(customerId);
            customer.remove();
        } catch (Exception ex) {
            throw new EJBException("removeCustomer: " + ex.getMessage());
        }
    }

    // getters
    public CustomerDetails getDetails(String customerId)
        throws CustomerNotFoundException, InvalidParameterException {
        // returns the CustomerDetails for the specified customer
        Debug.print("CustomerControllerBean getDetails");

        CustomerDetails result;

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        try {
            LocalCustomer customer = customerHome.findByPrimaryKey(customerId);
            result = new CustomerDetails(customer.getLastName(),
                    customer.getFirstName(), customer.getMiddleInitial(),
                    customer.getStreet(), customer.getCity(),
                    customer.getState(), customer.getZip(),
                    customer.getPhone(), customer.getEmail());
        } catch (FinderException ex) {
            throw new CustomerNotFoundException();
        } catch (Exception ex) {
            throw new EJBException("getDetails: " + ex.getMessage());
        }

        return result;
    }

    public ArrayList getCustomersOfAccount(String accountId)
        throws InvalidParameterException {
        // returns an ArrayList of CustomerDetails 
        // that correspond to the accountId specified
        Debug.print("CustomerControllerBean getCustomersOfAccount");

        Collection customers = null;

        if (accountId == null) {
            throw new InvalidParameterException("null accountId");
        }

        try {
            LocalAccount account = accountHome.findByPrimaryKey(accountId);
            customers = account.getCustomers();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyCustomersToDetails(customers);
    }

    public ArrayList getCustomersOfLastName(String lastName)
        throws InvalidParameterException {
        // returns an ArrayList of CustomerDetails 
        // that correspond to the the lastName specified
        // returns null if no customers are found
        Debug.print("CustomerControllerBean getCustomersOfLastName");

        Collection customers = null;

        if (lastName == null) {
            throw new InvalidParameterException("null lastName");
        }

        try {
            customers = customerHome.findByLastName(lastName);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyCustomersToDetails(customers);
    }

    // setters
    public void setName(String lastName, String firstName,
        String middleInitial, String customerId)
        throws CustomerNotFoundException, InvalidParameterException {
        Debug.print("CustomerControllerBean setName");

        if (lastName == null) {
            throw new InvalidParameterException("null lastName");
        }

        if (firstName == null) {
            throw new InvalidParameterException("null firstName");
        }

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        if (customerExists(customerId) == false) {
            throw new CustomerNotFoundException(customerId);
        }

        try {
            LocalCustomer customer = customerHome.findByPrimaryKey(customerId);
            customer.setLastName(lastName);
            customer.setFirstName(firstName);
            customer.setMiddleInitial(middleInitial);
        } catch (Exception ex) {
            throw new EJBException("setName: " + ex.getMessage());
        }
    }

    public void setAddress(String street, String city, String state,
        String zip, String phone, String email, String customerId)
        throws CustomerNotFoundException, InvalidParameterException {
        Debug.print("CustomerControllerBean setAddress");

        if (street == null) {
            throw new InvalidParameterException("null street");
        }

        if (city == null) {
            throw new InvalidParameterException("null city");
        }

        if (state == null) {
            throw new InvalidParameterException("null state");
        }

        if (customerId == null) {
            throw new InvalidParameterException("null customerId");
        }

        try {
            LocalCustomer customer = customerHome.findByPrimaryKey(customerId);
            customer.setStreet(street);
            customer.setCity(city);
            customer.setState(state);
            customer.setZip(zip);
            customer.setPhone(phone);
            customer.setEmail(email);
        } catch (Exception ex) {
            throw new EJBException("setAddress: " + ex.getMessage());
        }
    }

    public void ejbCreate() {
        Debug.print("CustomerControllerBean ejbCreate");

        try {
            customerHome = EJBGetter.getCustomerHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (NamingException ex) {
            Debug.print("CustomerControllerBean catch");
            throw new EJBException("ejbCreate: " + ex.getMessage());
        }
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
        Debug.print("CustomerControllerBean ejbActivate");

        try {
            customerHome = EJBGetter.getCustomerHome();
            accountHome = EJBGetter.getAccountHome();
            nextIdHome = EJBGetter.getNextIdHome();
        } catch (Exception ex) {
            Debug.print("CustomerControllerBean catch");
            throw new EJBException("ejbActivate: " + ex.getMessage());
        }
    }

    public void ejbPassivate() {
        customerHome = null;
        accountHome = null;
        nextIdHome = null;
    }

    public void setSessionContext(SessionContext sc) {
    }

    // private methods
    private boolean customerExists(String customerId) {
        // If a business method has been invoked with
        // a different customerId, then update the
        // customerId and customer variables.
        // Return null if the customer is not found.
        LocalCustomer customer = null;

        Debug.print("CustomerControllerBean customerExists");

        if (customerId.equals(this.customerId) == false) {
            try {
                customer = customerHome.findByPrimaryKey(customerId);
                this.customerId = customerId;
            } catch (Exception ex) {
                return false;
            }
        }

        return true;
    }

    private ArrayList copyCustomersToDetails(Collection customers) {
        ArrayList detailsList = new ArrayList();
        Iterator i = customers.iterator();

        try {
            while (i.hasNext()) {
                LocalCustomer customer = (LocalCustomer) i.next();
                CustomerDetails details =
                    new CustomerDetails(customer.getCustomerId(),
                        customer.getLastName(), customer.getFirstName(),
                        customer.getMiddleInitial(), customer.getStreet(),
                        customer.getCity(), customer.getState(),
                        customer.getZip(), customer.getPhone(),
                        customer.getEmail());
                detailsList.add(details);
            }
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return detailsList;
    }
} // CustomerControllerBean
