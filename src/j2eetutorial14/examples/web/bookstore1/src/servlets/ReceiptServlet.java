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
import cart.ShoppingCart;
import database.*;
import exception.*;


/**
 * An HTTP servlet that responds to the POST method of the HTTP protocol.
 * The Receipt servlet updates the book database inventory, invalidates the user session,
 * thanks the user for the order. */
public class ReceiptServlet extends HttpServlet {
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

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        boolean orderCompleted = true;

        // Get the user's session and shopping cart
        HttpSession session = request.getSession(true);
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        // Update the inventory
        try {
            bookDB.buyBooks(cart);
        } catch (OrderException e) {
            System.err.println(e.getMessage());
            orderCompleted = false;
        }

        // Payment received -- invalidate the session
        session.invalidate();

        // set content type header before accessing the Writer
        response.setContentType("text/html");
        response.setBufferSize(8192);

        PrintWriter out = response.getWriter();

        // then write the response
        out.println("<html>" + "<head><title>" +
            messages.getString("TitleReceipt") + "</title></head>");

        // Get the dispatcher; it gets the banner to the user
        RequestDispatcher dispatcher =
            getServletContext()
                .getRequestDispatcher("/banner");

        if (dispatcher != null) {
            dispatcher.include(request, response);
        }

        if (orderCompleted) {
            out.println("<h3>" + messages.getString("ThankYou") +
                request.getParameter("cardname") + ".");
        } else {
            out.println("<h3>" + messages.getString("OrderError"));
        }

        out.println("<p> &nbsp; <p><strong><a href=\"" +
            response.encodeURL(request.getContextPath()) + "/bookstore\">" +
            messages.getString("ContinueShopping") +
            "</a> &nbsp; &nbsp; &nbsp;" + "</body></html>");
        out.close();
    }

    public String getServletInfo() {
        return "The Receipt servlet updates the book database inventory, invalidates the user session, " +
        "thanks the user for the order.";
    }
}
