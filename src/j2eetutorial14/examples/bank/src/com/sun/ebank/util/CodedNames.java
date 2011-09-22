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


package com.sun.ebank.util;


/**
 * This interface defines names in code used as args for lookup().
 */
public interface CodedNames {
    public static final String ACCOUNT_EJBHOME = "java:comp/env/ejb/account";
    public static final String ACCOUNT_CONTROLLER_EJBHOME =
        "java:comp/env/ejb/accountController";
    public static final String CUSTOMER_EJBHOME = "java:comp/env/ejb/customer";
    public static final String CUSTOMER_CONTROLLER_EJBHOME =
        "java:comp/env/ejb/customerController";
    public static final String TX_EJBHOME = "java:comp/env/ejb/tx";
    public static final String TX_CONTROLLER_EJBHOME =
        "java:comp/env/ejb/txController";
    public static final String NEXT_ID_EJBHOME = "java:comp/env/ejb/nextId";
}
