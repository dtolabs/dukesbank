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

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.el.ValueBinding;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;
import javax.faces.event.ActionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Backing file bean for orderForm of CoffeeBreak demo.</p>
 */
public class CoffeeBreakBean {
    private static Log log = LogFactory.getLog(CoffeeBreakBean.class);
    public static final String CB_RESOURCE_BUNDLE_NAME =
        "com.sun.cb.messages.CBMessages";
    private static ResourceBundle bundle = null;
    private RetailPriceList retailPriceList = null;
    private ShoppingCart cart = null;

    public CoffeeBreakBean() {
        // load the different types of Coffee and their price list.
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext extcontext = context.getExternalContext();
        Map applicationMap = context.getExternalContext()
                                    .getApplicationMap();
        RetailPriceList rpl =
            (RetailPriceList) applicationMap.get("retailPriceList");

        if (retailPriceList == null) {
            try {
                retailPriceList = new RetailPriceList();
                applicationMap.put("retailPriceList", rpl);
            } catch (Exception ex) {
                log.error(
                    "CoffeeBreakBean: Couldn't create retail price list: " +
                    ex.getMessage());
            }
        }

        // populate the shopping cart with the price list
        if (cart == null) {
            cart = new ShoppingCart(retailPriceList);
        }
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public RetailPriceList getRetailPriceList() {
        return retailPriceList;
    }

    /*
     * Handles "Update" action on the orderForm
     */
    public String update() {
        if (log.isTraceEnabled()) {
            log.trace("CoffeeBreakBean: processOrderUpdate ");
        }

        updateCart();

        return null;
    }

    /*
     * Handles "Checkout" action on the orderForm
     */
    public String checkout() {
        if (log.isTraceEnabled()) {
            log.trace("CoffeeBreakBean: processCheckout ");
        }

        updateCart();

        return "checkout";
    }

    /*
     * Handles "Clear" action on the orderForm
     */
    public String clear() {
        if (log.isTraceEnabled()) {
            log.trace("CoffeeBreakBean: processClear ");
        }

        for (Iterator i = cart.getItems()
                              .iterator(); i.hasNext();) {
            ShoppingCartItem sci = (ShoppingCartItem) i.next();
            RetailPriceItem item = sci.getItem();
            sci.setPounds(new BigDecimal("0.0"));
            sci.setPrice(new BigDecimal("0.00"));
            cart.setTotal(new BigDecimal("0.00"));
        }

        return null;
    }

    /*
     * Updates the price on the Shopping Cart based on the quantity of Coffee
     * beans ordered.
     */
    public void updateCart() {
        BigDecimal total = new BigDecimal("0.00");
        BigDecimal price = new BigDecimal("0.00");

        for (Iterator i = cart.getItems()
                              .iterator(); i.hasNext();) {
            ShoppingCartItem sci = (ShoppingCartItem) i.next();
            RetailPriceItem item = sci.getItem();
            BigDecimal pounds = sci.getPounds();

            if (pounds != null) {
                price = item.getRetailPricePerPound()
                            .multiply(pounds)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                price = new BigDecimal("0.00");
                sci.setPounds(new BigDecimal("0.0"));
            }

            sci.setPrice(price);
            total = total.add(sci.getPrice())
                         .setScale(2);
            cart.setTotal(total);
        }
    }

    public String continueShopping() {
        clear();

        return "continue";
    }

    /**
     * Returns a localized text by looking up the resource bundle with the
     * given basename and key.
     */
    public static String loadErrorMessage(FacesContext context,
        String basename, String key) {
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle(basename,
                        context.getViewRoot().getLocale());
            } catch (Exception e) {
                return null;
            }
        }

        return bundle.getString(key);
    }
}
