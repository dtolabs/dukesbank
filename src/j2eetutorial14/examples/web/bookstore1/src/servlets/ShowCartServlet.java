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


package servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import database.*;
import cart.*;
import util.Currency;
import exception.*;


/**
 * An HTTP servlet that displays the contents of a customer's shopping
 * cart at Duke's Bookstore.  It responds to the GET and HEAD methods of
 * the HTTP protocol.  This servlet calls other servlets.
 */
public class ShowCartServlet extends HttpServlet {
    private BookDBAO bookDB;

    public void init() throws ServletException {
        bookDB = (BookDBAO) getServletContext()
                                .getAttribute("bookDB");

        if (bookDB == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    public void destroy() {
        bookDB = null;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // Get the user's session and shopping cart
        HttpSession session = request.getSession(true);
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        // If the user has no cart, create a new one
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        // set content type header before accessing the Writer
        response.setContentType("text/html");
        response.setBufferSize(8192);

        PrintWriter out = response.getWriter();

        //Print out the response
        out.println("<html>" + "<head><title>" +
            messages.getString("TitleShoppingCart") + "</title></head>");

        // Get the dispatcher; it gets the banner to the user
        RequestDispatcher dispatcher =
            getServletContext()
                .getRequestDispatcher("/banner");

        if (dispatcher != null) {
            dispatcher.include(request, response);
        }

        /* Handle any pending deletes from the shopping cart and
           indicate the outcome as part of the response */
        String bookId = request.getParameter("Remove");
        BookDetails bd;

        if (bookId != null) {
            try {
                bd = bookDB.getBookDetails(bookId);
                cart.remove(bookId);
                out.println("<font color=\"#ff00000\" size=\"+2\">" +
                    messages.getString("CartRemoved") + "<strong>" +
                    bd.getTitle() + "</strong> <br> &nbsp; <br>" + "</font>");
            } catch (BookNotFoundException ex) {
                response.reset();
                throw new ServletException(ex);
            }
        } else if (request.getParameter("Clear") != null) {
            cart.clear();
            out.println("<font color=\"#ff0000\" size=\"+2\"><strong>" +
                messages.getString("CartCleared") +
                "</strong> <br>&nbsp; <br> </font>");
        }

        // Print a summary of the shopping cart
        int num = cart.getNumberOfItems();

        if (num > 0) {
            out.println("<font size=\"+2\">" +
                messages.getString("CartContents") + num +
                ((num == 1) ? messages.getString("CartItem")
                            : messages.getString("CartItems")) +
                "</font><br>&nbsp;");

            // Return the Shopping Cart 
            out.println("<table summary=\"layout\">" + "<tr>" +
                "<th align=left>" + messages.getString("ItemQuantity") +
                "</TH>" + "<th align=left>" + messages.getString("ItemTitle") +
                "</TH>" + "<th align=left>" + messages.getString("ItemPrice") +
                "</TH>" + "</tr>");

            Iterator i = cart.getItems()
                             .iterator();
            Currency c = (Currency) session.getAttribute("currency");

            if (c == null) {
                c = new Currency();
                c.setLocale(request.getLocale());
                session.setAttribute("currency", c);
            }

            while (i.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) i.next();
                bd = (BookDetails) item.getItem();
                c.setAmount(bd.getPrice());

                out.println("<tr>" +
                    "<td align=\"right\" bgcolor=\"#ffffff\">" +
                    item.getQuantity() + "</td>" + "<td bgcolor=\"#ffffaa\">" +
                    "<strong><a href=\"" +
                    response.encodeURL(request.getContextPath() +
                        "/bookdetails?bookId=" + bd.getBookId()) + "\">" +
                    bd.getTitle() + "</a></strong>" + "</td>" +
                    "<td bgcolor=\"#ffffaa\" align=\"right\">" + c.getFormat() +
                    "</td>" + "<td bgcolor=\"#ffffaa\">" + "<strong>" +
                    "<a href=\"" +
                    response.encodeURL(request.getContextPath() +
                        "/bookshowcart?Remove=" + bd.getBookId()) + "\">" +
                    messages.getString("RemoveItem") + "</a></strong>" +
                    "</td></tr>");
            }

            c.setAmount(cart.getTotal());
            // Print the total at the bottom of the table
            out.println("<tr><td colspan=\"5\" bgcolor=\"#ffffff\">" +
                "<br></td></tr>" + "<tr>" +
                "<td colspan=\"2\" align=\"right\"" + "bgcolor=\"#ffffff\">" +
                messages.getString("Subtotal") + "</td>" +
                "<td bgcolor=\"#ffffaa\" align=\"right\">" + c.getFormat() +
                "</td>" + "</td><td><br></td></tr></table>");

            // Where to go and what to do next
            out.println("<p> &nbsp; <p><strong><a href=\"" +
                response.encodeURL(request.getContextPath() + "/bookcatalog") +
                "\">" + messages.getString("ContinueShopping") +
                "</a> &nbsp; &nbsp; &nbsp;" + "<a href=\"" +
                response.encodeURL(request.getContextPath() + "/bookcashier") +
                "\">" + messages.getString("Checkout") +
                "</a> &nbsp; &nbsp; &nbsp;" + "<a href=\"" +
                response.encodeURL(request.getContextPath() +
                    "/bookshowcart?Clear=clear") + "\">" +
                messages.getString("ClearCart") + "</a></strong>");
        } else {
            // Shopping cart is empty!
            out.println("<font size=\"+2\">" + messages.getString("CartEmpty") +
                "</font>" + "<br> &nbsp; <br>" + "<center><a href=\"" +
                response.encodeURL(request.getContextPath() + "/bookcatalog") +
                "\">" + messages.getString("Catalog") + "</a> </center>");
        }

        out.println("</body> </html>");
        out.close();
    }

    public String getServletInfo() {
        return "The ShowCart servlet returns information about" +
        "the books that the user is in the process of ordering.";
    }
}
