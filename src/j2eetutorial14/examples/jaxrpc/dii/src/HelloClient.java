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


package dii;

import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.JAXRPCException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.ParameterMode;


public class HelloClient {
    private static String qnameService = "MyHelloService";
    private static String qnamePort = "HelloIF";
    private static String BODY_NAMESPACE_VALUE = "urn:Foo";
    private static String ENCODING_STYLE_PROPERTY =
        "javax.xml.rpc.encodingstyle.namespace.uri";
    private static String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    private static String URI_ENCODING =
        "http://schemas.xmlsoap.org/soap/encoding/";

    public static void main(String[] args) {
        System.out.println("Endpoint address = " + args[0]);

        try {
            ServiceFactory factory = ServiceFactory.newInstance();
            Service service = factory.createService(new QName(qnameService));

            QName port = new QName(qnamePort);

            Call call = service.createCall(port);

            call.setTargetEndpointAddress(args[0]);
            call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
            call.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
            call.setProperty(ENCODING_STYLE_PROPERTY, URI_ENCODING);

            QName QNAME_TYPE_STRING = new QName(NS_XSD, "string");

            call.setReturnType(QNAME_TYPE_STRING);

            call.setOperationName(new QName(BODY_NAMESPACE_VALUE, "sayHello"));
            call.addParameter("String_1", QNAME_TYPE_STRING, ParameterMode.IN);

            String[] params = { "Murph!" };

            String result = (String) call.invoke(params);

            System.out.println(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
