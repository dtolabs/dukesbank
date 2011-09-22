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


package com.sun.cb;

import java.io.Serializable;
import java.lang.Number;
import java.util.logging.*;


/**
 * <p>JavaBean representing the data for an individual customer.</p>
 *
 */
public class CoffeeBean implements Serializable {
    static final Logger logger =
        Logger.getLogger("server-jsf.com.sun.cb.CoffeeBean");
    private String coffeeName = null;
    private double pricePerPound = 0.0;
    private double total = 0.0;

    public CoffeeBean(String coffeeName, double pricePerPound) {
        logger.info("Created CoffeeBean");
        this.coffeeName = coffeeName;
        this.pricePerPound = pricePerPound;
    }

    public String getCoffeeName() {
        return (this.coffeeName);
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public double getPricePerPound() {
        return (this.pricePerPound);
    }

    public void setPricePerPound(double pricePerPound) {
        this.pricePerPound = pricePerPound;
    }

    public double getTotal() {
        return (this.total);
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
