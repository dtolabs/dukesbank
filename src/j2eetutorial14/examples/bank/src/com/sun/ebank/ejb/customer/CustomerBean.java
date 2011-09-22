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
import com.sun.ebank.util.Debug;
import com.sun.ebank.util.CustomerDetails;
import com.sun.ebank.util.CodedNames;


public abstract class CustomerBean implements EntityBean {
    private EntityContext context;

    // Access methods for persistent fields
    public abstract String getCustomerId();

    public abstract void setCustomerId(String id);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getMiddleInitial();

    public abstract void setMiddleInitial(String middleInitial);

    public abstract String getStreet();

    public abstract void setStreet(String street);

    public abstract String getCity();

    public abstract void setCity(String city);

    public abstract String getState();

    public abstract void setState(String state);

    public abstract String getZip();

    public abstract void setZip(String zip);

    public abstract String getPhone();

    public abstract void setPhone(String phone);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    // Access methods for relationship fields
    public abstract Collection getAccounts();

    public abstract void setAccounts(Collection accounts);

    // Business methods

    // ejb methods
    public String ejbCreate(String customerId, String lastName,
        String firstName, String middleInitial, String street, String city,
        String state, String zip, String phone, String email)
        throws CreateException {
        Debug.print("CustomerBean ejbCreate");

        setCustomerId(customerId);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleInitial(middleInitial);
        setStreet(street);
        setCity(city);
        setState(state);
        setZip(zip);
        setPhone(phone);
        setEmail(email);

        return null;
    }

    public void ejbRemove() {
        Debug.print("CustomerBean ejbRemove");
    }

    public void setEntityContext(EntityContext ctx) {
        Debug.print("CustomerBean setEntityContext");
        context = ctx;
    }

    public void unsetEntityContext() {
        Debug.print("CustomerBean unsetEntityContext");
        context = null;
    }

    public void ejbLoad() {
        Debug.print("CustomerBean ejbLoad");
    }

    public void ejbStore() {
        Debug.print("CustomerBean ejbStore");
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate(String customerId, String lastName,
        String firstName, String middleInitial, String street, String city,
        String state, String zip, String phone, String email) {
    }
}
