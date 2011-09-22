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
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.*;
import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;


public class RetailPriceList implements Serializable {
    static final Logger logger =
        Logger.getLogger("server.com.sun.cb.RetailPriceList");
    private ArrayList retailPriceItems;
    private ArrayList suppliers;

    public RetailPriceList() {
        String RPCSupplier = "JAXRPCCoffeeSupplier";
        retailPriceItems = new ArrayList();
        suppliers = new ArrayList();

        JAXRQueryByName jq = new JAXRQueryByName();
        Connection connection = jq.makeConnection();
        logger.info(RPCSupplier);

        Collection orgs = jq.executeQuery(RPCSupplier);
        Iterator orgIter = orgs.iterator();

        // Display organization information
        try {
            while (orgIter.hasNext()) {
                Organization org = (Organization) orgIter.next();
                logger.info("Org name: " + jq.getName(org));
                logger.info("Org description: " + jq.getDescription(org));
                logger.info("Org key id: " + jq.getKey(org));

                // Display service and binding information
                Collection services = org.getServices();
                Iterator svcIter = services.iterator();

                while (svcIter.hasNext()) {
                    Service svc = (Service) svcIter.next();
                    logger.info(" Service name: " + jq.getName(svc));
                    logger.info(" Service description: " +
                        jq.getDescription(svc));

                    Collection serviceBindings = svc.getServiceBindings();
                    Iterator sbIter = serviceBindings.iterator();

                    while (sbIter.hasNext()) {
                        ServiceBinding sb = (ServiceBinding) sbIter.next();
                        String supplier = sb.getAccessURI();
                        logger.info("  Binding Description: " +
                            jq.getDescription(sb));
                        logger.info("  Access URI: " + supplier);

                        // Get price list from service at supplier URI
                        PriceListBean priceList =
                            PriceFetcher.getPriceList(supplier);

                        PriceItemBean[] items = priceList.getPriceItems();
                        retailPriceItems = new ArrayList();
                        suppliers = new ArrayList();

                        BigDecimal price = new BigDecimal("0.00");

                        for (int i = 0; i < items.length; i++) {
                            price = items[i].getPricePerPound()
                                            .multiply(new BigDecimal("1.35"))
                                            .setScale(2,
                                    BigDecimal.ROUND_HALF_UP);

                            RetailPriceItem pi =
                                new RetailPriceItem(items[i].getCoffeeName(),
                                    items[i].getPricePerPound(), price, supplier);
                            retailPriceItems.add(pi);
                        }

                        suppliers.add(supplier);
                    }
                }

                // Print spacer between organizations
                logger.info(" --- ");
            }
        } catch (Exception e) {
            logger.severe("RetailPriceList: Exception: " + e.toString());
        } finally {
            // At end, close connection to registry
            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                }
            }
        }

        String SAAJPriceListURL = URLHelper.getSaajURL() + "/getPriceList";
        String SAAJOrderURL = URLHelper.getSaajURL() + "/orderCoffee";
        PriceListRequest plr = new PriceListRequest(SAAJPriceListURL);
        PriceListBean priceList = plr.getPriceList();
        ;

        PriceItemBean[] priceItems = priceList.getPriceItems();

        for (int i = 0; i < priceItems.length; i++) {
            PriceItemBean pib = priceItems[i];
            BigDecimal price =
                pib.getPricePerPound()
                   .multiply(new BigDecimal("1.35"))
                   .setScale(2, BigDecimal.ROUND_HALF_UP);
            RetailPriceItem rpi =
                new RetailPriceItem(pib.getCoffeeName(),
                    pib.getPricePerPound(), price, SAAJOrderURL);
            retailPriceItems.add(rpi);
        }

        suppliers.add(SAAJOrderURL);
    }

    public ArrayList getItems() {
        return retailPriceItems;
    }

    public ArrayList getSuppliers() {
        return suppliers;
    }

    public void setItems(ArrayList priceItems) {
        this.retailPriceItems = priceItems;
    }

    public void setSuppliers(ArrayList suppliers) {
        this.suppliers = suppliers;
    }
}
