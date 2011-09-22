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


package backing;

import cart.ShoppingCart;
import cart.ShoppingCartItem;
import database.BookDetails;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import database.BookDBAO;


/**
 * <p>Abstract base class for backing beans to share utility methods.</p>
 */
public abstract class AbstractBean {
    // ----------------------------------------------------- Application Actions

    /**
     * <p>Buy the items currently in the shopping cart.</p>
     */
    public String buy() {
        // "Cannot happen" sanity check
        if (cart()
                    .getNumberOfItems() < 1) {
            message(null, "CartEmpty");

            return (null);
        } else {
            return ("purchase");
        }
    }

    /**
     * <p>Clear all items from the shopping cart.</p>
     */
    public String clear() {
        cart()
            .clear();
        message(null, "CartCleared");

        return (null);
    }

    // ------------------------------------------------------- Protected Methods

    /**
     * <p>Return the currently selected <code>BookDetails</code> instance
     * from the user request.</p>
     */
    protected BookDetails book() {
        BookDetails book =
            (BookDetails) context()
                              .getExternalContext()
                              .getRequestMap()
                              .get("book");

        return (book);
    }

    /**
     * <p>Return the <code>ShoppingCart</code> instance from the
     * user session.</p>
     */
    protected ShoppingCart cart() {
        FacesContext context = context();
        ValueBinding vb =
            context.getApplication()
                   .createValueBinding("#{cart}");

        return ((ShoppingCart) vb.getValue(context));
    }

    /**
     * <p>Return the <code>FacesContext</code> instance for the
     * current request.
     */
    protected FacesContext context() {
        return (FacesContext.getCurrentInstance());
    }

    /**
     * <p>Return the <code>BookDBAO</code> object for our database.</p>
     */
    protected BookDBAO dbao() {
        BookDBAO dbao =
            (BookDBAO) context()
                           .getExternalContext()
                           .getApplicationMap()
                           .get("bookDBAO");

        return (dbao);
    }

    /**
     * <p>Return the <code>ShoppingCartItem</code> instance from the
     * user request.</p>
     */
    protected ShoppingCartItem item() {
        ShoppingCartItem item =
            (ShoppingCartItem) context()
                                   .getExternalContext()
                                   .getRequestMap()
                                   .get("item");

        return (item);
    }

    /**
     * <p>Add a localized message to the <code>FacesContext</code> for the
     * current request.</p>
     *
     * @param clientId Client identifier of the component this message
     *  relates to, or <code>null</code> for global messages
     * @param key Message key of the message to include
     */
    protected void message(String clientId, String key) {
        // Look up the requested message text
        String text = null;

        try {
            ResourceBundle bundle =
                ResourceBundle.getBundle("messages.BookstoreMessages",
                    context().getViewRoot().getLocale());
            text = bundle.getString(key);
        } catch (Exception e) {
            text = "???" + key + "???";
        }

        // Construct and add a FacesMessage containing it
        context()
            .addMessage(clientId, new FacesMessage(text));
    }

    /**
     * <p>Add a localized message to the <code>FacesContext</code> for the
     * current request.</p>
     *
     * @param clientId Client identifier of the component this message
     *  relates to, or <code>null</code> for global messages
     * @param key Message key of the message to include
     * @param params Substitution parameters for using the localized text
     *  as a message format
     */
    protected void message(String clientId, String key, Object[] params) {
        // Look up the requested message text
        String text = null;

        try {
            ResourceBundle bundle =
                ResourceBundle.getBundle("messages.BookstoreMessages",
                    context().getViewRoot().getLocale());
            text = bundle.getString(key);
        } catch (Exception e) {
            text = "???" + key + "???";
        }

        // Perform the requested substitutions
        if ((params != null) && (params.length > 0)) {
            text = MessageFormat.format(text, params);
        }

        // Construct and add a FacesMessage containing it
        context()
            .addMessage(clientId, new FacesMessage(text));
    }

    /**
     * <p>Return the explicitly selected <code>BookDetails</code> instance
     * from the user request.</p>
     */
    protected BookDetails selected() {
        BookDetails selected =
            (BookDetails) context()
                              .getExternalContext()
                              .getSessionMap()
                              .get("selected");

        return (selected);
    }
}
