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


package client;

import javax.ejb.EJBHome;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
import javax.jms.*;
import sb.*;


/**
 * The MyAppClient class is the client program for this J2EE
 * application.  It obtains a reference to the home interface
 * of the Publisher enterprise bean and creates an instance of
 * the bean.  After calling the publisher's publishNews method
 * twice, it removes the bean.
 */
public class MyAppClient {
    public static void main(String[] args) {
        MyAppClient client = new MyAppClient();
        client.doTest();
        System.exit(0);
    }

    public void doTest() {
        try {
            Context ic = new InitialContext();

            System.out.println("Looking up EJB reference");

            java.lang.Object objref =
                ic.lookup("java:comp/env/ejb/remote/Publisher");
            System.err.println("Looked up home");

            PublisherHome pubHome =
                (PublisherHome) PortableRemoteObject.narrow(objref,
                    PublisherHome.class);
            System.err.println("Narrowed home");

            /*
             * Create bean instance, invoke business method
             * twice, and remove bean instance.
             */
            PublisherRemote phr = pubHome.create();
            System.err.println("Got the EJB");
            phr.publishNews();
            phr.publishNews();
            phr.remove();
            System.out.println("To view the bean output,");
            System.out.println(
                " check <install_dir>/domains/domain1/logs/server.log.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
