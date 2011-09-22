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


package mypkg;

import java.util.*;
import java.text.DateFormat;


public class MyLocales {
    HashMap locales;
    ArrayList localeNames;
    Locale selectedLocale;
    String selectedLocaleString;

    public MyLocales() {
        locales = new HashMap();
        localeNames = new ArrayList();

        Locale[] list = DateFormat.getAvailableLocales();

        for (int i = 0; i < list.length; i++) {
            locales.put(list[i].getDisplayName(), list[i]);
            localeNames.add(list[i].getDisplayName());
        }

        Collections.sort(localeNames);
        selectedLocale = null;
        selectedLocaleString = null;
    }

    public static boolean equals(String l1, String l2) {
        return l1.equals(l2);
    }

    public Collection getLocaleNames() {
        return localeNames;
    }

    public void setSelectedLocaleString(String displayName) {
        this.selectedLocaleString = displayName;
        this.selectedLocale = (Locale) locales.get(displayName);
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public String getSelectedLocaleString() {
        return selectedLocaleString;
    }
}
