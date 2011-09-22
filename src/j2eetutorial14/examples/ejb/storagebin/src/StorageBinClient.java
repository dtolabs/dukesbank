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


public class StorageBinClient {
    public static void main(String[] args) {
        try {
            Context initial = new InitialContext();
            Object objref =
                initial.lookup("java:comp/env/ejb/SimpleStorageBin");

            StorageBinHome storageBinHome =
                (StorageBinHome) PortableRemoteObject.narrow(objref,
                    StorageBinHome.class);

            objref = initial.lookup("java:comp/env/ejb/SimpleWidget");

            WidgetHome widgetHome =
                (WidgetHome) PortableRemoteObject.narrow(objref,
                    WidgetHome.class);

            String widgetId = "777";
            StorageBin storageBin = storageBinHome.findByWidgetId(widgetId);
            String storageBinId = (String) storageBin.getPrimaryKey();
            int quantity = storageBin.getQuantity();

            Widget widget = widgetHome.findByPrimaryKey(widgetId);
            double price = widget.getPrice();
            String description = widget.getDescription();

            System.out.println(widgetId + " " + storageBinId + " " + quantity +
                " " + price + " " + description);

            System.exit(0);
        } catch (Exception ex) {
            System.err.println("Caught an exception.");
            ex.printStackTrace();
        }
    }
}
