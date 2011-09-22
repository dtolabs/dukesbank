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


package appclient;

import javax.xml.rpc.Stub;
import javax.naming.*;


public class HelloClient {
    private String endpointAddress;

    public static void main(String[] args) {
        String argument = new String();

        if (args.length > 0) {
            argument = args[0];
        } else {
            argument = "http://localhost:8080/hello-jaxrpc/hello";
        }

        System.out.println("Endpoint address = " + argument);

        try {
            Context ic = new InitialContext();
            MyHelloService myHelloService =
                (MyHelloService) ic.lookup(
                    "java:comp/env/service/MyJAXRPCHello");
            appclient.HelloIF helloPort = myHelloService.getHelloIFPort();

            ((Stub) helloPort)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,
                argument);

            System.out.println(helloPort.sayHello("Jake!"));
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    } // main
}
