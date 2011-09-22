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
import javax.ejb.*;


public class CartBean implements SessionBean {
    String customerName;
    String customerId;
    Vector contents;

    public CartBean() {
    }

    public void ejbCreate(String person) throws CreateException {
        if (person == null) {
            throw new CreateException("Null person not allowed.");
        } else {
            customerName = person;
        }

        customerId = "0";
        contents = new Vector();
    }

    public void ejbCreate(String person, String id) throws CreateException {
        if (person == null) {
            throw new CreateException("Null person not allowed.");
        } else {
            customerName = person;
        }

        IdVerifier idChecker = new IdVerifier();

        if (idChecker.validate(id)) {
            customerId = id;
        } else {
            throw new CreateException("Invalid id: " + id);
        }

        contents = new Vector();
    }

    public void addBook(String title) {
        contents.addElement(title);
    }

    public void removeBook(String title) throws BookException {
        boolean result = contents.removeElement(title);

        if (result == false) {
            throw new BookException(title + " not in cart.");
        }
    }

    public Vector getContents() {
        return contents;
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void setSessionContext(SessionContext sc) {
    }
}
