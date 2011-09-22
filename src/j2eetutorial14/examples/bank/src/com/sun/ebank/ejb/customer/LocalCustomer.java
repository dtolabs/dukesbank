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
import com.sun.ebank.util.CustomerDetails;


public interface LocalCustomer extends EJBLocalObject {
    public String getCustomerId();

    public String getLastName();

    public String getFirstName();

    public String getMiddleInitial();

    public String getStreet();

    public String getCity();

    public String getState();

    public String getZip();

    public String getPhone();

    public String getEmail();

    public Collection getAccounts();

    public void setLastName(String lastName);

    public void setFirstName(String firstName);

    public void setMiddleInitial(String middleInitial);

    public void setStreet(String street);

    public void setCity(String city);

    public void setState(String state);

    public void setZip(String zip);

    public void setPhone(String phone);

    public void setEmail(String email);
}
