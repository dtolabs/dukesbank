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


import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;


public class SalesRepClient {
    public static void main(String[] args) {
        try {
            Context initial = new InitialContext();

            Object objref = initial.lookup("java:comp/env/ejb/SimpleSalesRep");
            SalesRepHome salesHome =
                (SalesRepHome) PortableRemoteObject.narrow(objref,
                    SalesRepHome.class);

            objref = initial.lookup("java:comp/env/ejb/SimpleCustomer");

            CustomerHome customerHome =
                (CustomerHome) PortableRemoteObject.narrow(objref,
                    CustomerHome.class);

            Customer buzz = customerHome.create("844", "543", "Buzz Murphy");

            Collection c = customerHome.findBySalesRep("543");
            Iterator i = c.iterator();

            while (i.hasNext()) {
                Customer customer = (Customer) i.next();
                String customerId = (String) customer.getPrimaryKey();

                System.out.println("customerId = " + customerId);
            }

            System.out.println();

            Customer mary = customerHome.findByPrimaryKey("987");

            mary.setSalesRepId("543");

            Customer x = customerHome.findByPrimaryKey("987");
            SalesRep janice = salesHome.findByPrimaryKey("543");
            ArrayList a = janice.getCustomerIds();

            i = a.iterator();

            while (i.hasNext()) {
                String customerId = (String) i.next();
                Customer customer = customerHome.findByPrimaryKey(customerId);
                String name = customer.getName();

                System.out.println(customerId + ": " + name);
            }

            System.exit(0);
        } catch (Exception ex) {
            System.err.println("Caught an exception.");
            ex.printStackTrace();
        }
    }
}
