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
import database.BookDBAO;
import database.BookDetails;
import exception.BooksNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


/**
 * <p>Backing Bean for the <code>/bookstore.jsp</code> page.</p>
 */
public class BookstoreBean extends AbstractBean {
    // -------------------------------------------------------------- Properties
    private BookDetails featured = null;

    /**
     * <p>Return the <code>BookDetails</code> for the featured book.</p>
     */
    public BookDetails getFeatured() {
        if (featured == null) {
            try {
                featured = (BookDetails) dbao()
                                             .getBooks()
                                             .get(5);
            } catch (BooksNotFoundException e) {
                // Real apps would deal with error conditions better than this
                throw new FacesException(e);
            }
        }

        return (featured);
    }

    // ----------------------------------------------------- Application Actions

    /**
     * <p>Add the selected item to our shopping cart.</p>
     */
    public String add() {
        BookDetails book = getFeatured();
        cart()
            .add(book.getBookId(), book);
        message(null, "ConfirmAdd", new Object[] { book.getTitle() });

        return ("catalog");
    }

    /**
     * <p>Show the details page for the current book.</p>
     */
    public String details() {
        context()
            .getExternalContext()
            .getSessionMap()
            .put("selected", getFeatured());

        return ("details");
    }
}
