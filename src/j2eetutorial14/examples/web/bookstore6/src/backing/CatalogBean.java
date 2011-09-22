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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIOutput;
import javax.faces.convert.IntegerConverter;
import javax.faces.el.ValueBinding;
import java.lang.Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Backing Bean for the <code>/bookcatalog.jsp</code> page.</p>
 */
public class CatalogBean extends AbstractBean {
    // ----------------------------------------------------- Application Actions
    private static Log log = LogFactory.getLog(CatalogBean.class);
    private int totalBooks = 0;

    /**
     * <p>Add the selected item to our shopping cart.</p>
     */
    public String add() {
        BookDetails book = book();
        cart()
            .add(book.getBookId(), book);
        message(null, "ConfirmAdd", new Object[] { book.getTitle() });

        //  updateTotalBooks();
        return ("catalog");
    }

    /**
     * <p>Show the details page for the current book.</p>
     */
    public String details() {
        context()
            .getExternalContext()
            .getSessionMap()
            .put("selected", book());

        return ("details");
    }

    public int getTotalBooks() {
        totalBooks = cart()
                         .getNumberOfItems();

        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    //           totalBooks= cart().getNumberOfItems();

    //    }
    public int getBookQuantity() {
        int bookQuantity = 0;
        BookDetails book = book();

        if (book == null) {
            return bookQuantity;
        }

        List results = cart()
                           .getItems();

        for (Iterator items = results.iterator(); items.hasNext();) {
            ShoppingCartItem item = (ShoppingCartItem) items.next();
            BookDetails bd = (BookDetails) item.getItem();

            if ((bd.getBookId()).equals(book.getBookId())) {
                System.out.println("bd id " + bd.getBookId() + " book id " +
                    book.getBookId());
                bookQuantity = item.getQuantity();

                break;
            }
        }

        return bookQuantity;
    }
}
