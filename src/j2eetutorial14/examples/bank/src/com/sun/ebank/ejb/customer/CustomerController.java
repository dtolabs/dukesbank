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

import java.util.ArrayList;
import java.util.Date;
import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import com.sun.ebank.util.CustomerDetails;
import com.sun.ebank.ejb.exception.*;


public interface CustomerController extends EJBObject {
    // customer creation and removal methods

    // makes a new customer and enters it into db,
    // returns customerId
    public String createCustomer(CustomerDetails details)
        throws RemoteException, InvalidParameterException;

    // removes customer from db
    public void removeCustomer(String customerId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException;

    // getters

    // returns the details of a customer
    public CustomerDetails getDetails(String customerId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException;

    // returns an ArrayList of CustomerDetails objects
    // that correspond to the customers for the specified
    // account
    public ArrayList getCustomersOfAccount(String accountId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException;

    // returns an ArrayList of CustomerDetails objects
    // that correspond to the customers for the specified
    // last name; if now customers are found the ArrayList
    // is empty
    public ArrayList getCustomersOfLastName(String lastName)
        throws InvalidParameterException, RemoteException;

    // setters
    public void setName(String lastName, String firstName,
        String middleInitial, String customerId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException;

    public void setAddress(String street, String city, String state,
        String zip, String phone, String email, String customerId)
        throws RemoteException, CustomerNotFoundException, 
            InvalidParameterException;
} // CustomerController
