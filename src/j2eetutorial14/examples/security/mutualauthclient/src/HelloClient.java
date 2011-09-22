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


package mutualauthclient;

import javax.xml.rpc.Stub;


public class HelloClient {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("5 command-line parms required");
            System.exit(1);
        }

        String keyStore = args[0];
        String keyStorePassword = args[1];
        String trustStore = args[2];
        String trustStorePassword = args[3];
        String endpointAddress = args[4];

        System.out.println("keystore: " + keyStore);
        System.out.println("keystorePassword: " + keyStorePassword);
        System.out.println("trustStore: " + trustStore);
        System.out.println("trustStorePassword: " + trustStorePassword);
        System.out.println("Endpoint address = " + endpointAddress);

        try {
            Stub stub = createProxy();

            System.setProperty("javax.net.ssl.keyStore", keyStore);
            System.setProperty("javax.net.ssl.keyStorePassword",
                keyStorePassword);
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword",
                trustStorePassword);

            stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,
                endpointAddress);

            HelloIF hello = (HelloIF) stub;
            System.out.println();
            System.out.println(hello.sayHello(" Duke (secure)"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Stub createProxy() {
        return (Stub) (new MySecureHelloService_Impl().getHelloIFPort());
    }
}
