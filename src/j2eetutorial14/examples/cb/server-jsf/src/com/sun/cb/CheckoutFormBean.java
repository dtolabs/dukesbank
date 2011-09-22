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
import javax.faces.el.ValueBinding;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.application.FacesMessage;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.*;


public class CheckoutFormBean {
    static final Logger logger =
        Logger.getLogger("server-jsf.com.sun.cb.CheckoutFormBean");
    private String firstName;
    private String lastName;
    private String email;
    private String areaCode;
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String zip;
    private int CCOption;
    private String CCNumber;
    private OrderConfirmations ocs = null;

    /**
     * <p>Backing file bean for checkoutForm of CoffeeBreak demo.</p>
     */
    public CheckoutFormBean() {
    }

    public OrderConfirmations getOrderConfirmations() {
        return ocs;
    }

    public void setOrderConfirmations(OrderConfirmations newOrders) {
        this.ocs = ocs;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getZip() {
        return zip;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getCCOption() {
        return CCOption;
    }

    public String getCCNumber() {
        return CCNumber;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCCOption(int CCOption) {
        this.CCOption = CCOption;
    }

    public void setCCNumber(String CCNumber) {
        this.CCNumber = CCNumber;
    }

    /*
     * Processes the customer order by forwarding the order details to the supplier.
     */
    public String submit() {
        ocs = new OrderConfirmations();

        FacesContext context = FacesContext.getCurrentInstance();
        ValueBinding vb =
            context.getApplication()
                   .createValueBinding("#{CoffeeBreakBean}");
        CoffeeBreakBean cbBean = (CoffeeBreakBean) vb.getValue(context);

        RetailPriceList rpl = cbBean.getRetailPriceList();
        ShoppingCart cart = cbBean.getCart();

        ConfirmationBean confirmation = null;
        String orderId = CCNumber;

        AddressBean address = new AddressBean(street, city, state, zip);
        CustomerBean customer =
            new CustomerBean(firstName, lastName,
                "(" + areaCode + ") " + phoneNumber, email);

        for (Iterator d = rpl.getSuppliers()
                             .iterator(); d.hasNext();) {
            String supplier = (String) d.next();
            logger.info(supplier);

            ArrayList lis = new ArrayList();
            BigDecimal price = new BigDecimal("0.00");
            BigDecimal total = new BigDecimal("0.00");

            for (Iterator c = cart.getItems()
                                  .iterator(); c.hasNext();) {
                ShoppingCartItem sci = (ShoppingCartItem) c.next();

                if ((sci.getItem()
                            .getSupplier()).equals(supplier) &&
                        ((sci.getPounds()).floatValue() > 0)) {
                    price = sci.getItem()
                               .getWholesalePricePerPound()
                               .multiply(sci.getPounds());
                    total = total.add(price);

                    LineItemBean li =
                        new LineItemBean(sci.getItem().getCoffeeName(),
                            sci.getPounds(),
                            sci.getItem().getWholesalePricePerPound());
                    lis.add(li);
                }
            }

            LineItemBean[] lineItems = new LineItemBean[lis.size()];
            int i = 0;

            for (Iterator j = lis.iterator(); j.hasNext();) {
                lineItems[i] = (LineItemBean) j.next();
                i++;
            }

            if (!lis.isEmpty()) {
                OrderBean order =
                    new OrderBean(address, customer, orderId, lineItems, total);

                String SAAJOrderURL = URLHelper.getSaajURL() + "/orderCoffee";

                if (supplier.equals(SAAJOrderURL)) {
                    OrderRequest or = new OrderRequest(SAAJOrderURL);
                    confirmation = or.placeOrder(order);
                } else {
                    OrderCaller ocaller = new OrderCaller(supplier);
                    confirmation = ocaller.placeOrder(order);
                }

                OrderConfirmation oc =
                    new OrderConfirmation(order, confirmation);
                ocs.add(oc);
            }
        }

        return "submit";
    }

    /*
     * Clears the Customer information.
     */
    public String clear() {
        firstName = "";
        lastName = "";
        email = "";
        areaCode = "";
        phoneNumber = "";
        street = "";
        city = "";
        state = "";
        zip = "";
        CCOption = 0;
        CCNumber = "";

        return null;
    }

    /**
     * Validates the "email" field of checkoutForm. If it does not follow
     * the expected syntax, queues an error message.
     */
    public void validateEmail(FacesContext context, UIComponent toValidate,
        Object value) {
        String message = "";

        String email = (String) value;

        if (email.indexOf('@') == -1) {
            ((UIInput) toValidate).setValid(false);
            message = CoffeeBreakBean.loadErrorMessage(context,
                    CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
            context.addMessage(toValidate.getClientId(context),
                new FacesMessage(message));
        }
    }
}
