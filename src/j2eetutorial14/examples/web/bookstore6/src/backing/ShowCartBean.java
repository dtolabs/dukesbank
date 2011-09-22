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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.el.ValueBinding;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


/**
 * <p>Backing Bean for the <code>/bookshowcart.jsp</code> page.</p>
 */
public class ShowCartBean extends AbstractBean {
    // ----------------------------------------------------- Application Actions

    /**
     * <p>Show the details page for the current book.</p>
     */
    public String details() {
        context()
            .getExternalContext()
            .getSessionMap()
            .put("selected", item().getItem());

        return ("details");
    }

    /**
     * <p>Remove the item from the shopping cart.</p>
     */
    public String remove() {
        BookDetails book = (BookDetails) item()
                                             .getItem();
        cart()
            .remove(book.getBookId());
        message(null, "ConfirmRemove", new Object[] { book.getTitle() });

        return (null);
    }

    /**
     * <p>Update the quantities in the shopping cart, based on the
     * values entered in the "Quantity" column.</p>
     */
    public String update() {
        message(null, "QuantitiesUpdated");

        //    FacesContext context = context();
        //    ValueBinding vb =
        //        context.getApplication().createValueBinding("#{catalog.totalBooks.value}");
        //     vb.setValue(context, new Integer(cart().getNumberOfItems()));
        return (null);
    }
}
