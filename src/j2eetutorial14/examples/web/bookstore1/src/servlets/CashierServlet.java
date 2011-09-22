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
import cart.*;
import util.Currency;


/**
 * An HTTP Servlet that responds to the GET method of the
 * HTTP protocol.  It returns a form to the user that gathers data.
 * The form POSTs to another servlet.
 */
public class CashierServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // Get the user's session and shopping cart
        HttpSession session = request.getSession();
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        // set content-type header before accessing Writer
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        Currency c = (Currency) session.getAttribute("currency");

        if (c == null) {
            c = new Currency();
            c.setLocale(request.getLocale());
            session.setAttribute("currency", c);
        }

        c.setAmount(cart.getTotal());

        // then write the data of the response
        out.println("<html>" + "<head><title>" +
            messages.getString("TitleCashier") + "</title></head>");

        // Get the dispatcher; it gets the banner to the user
        RequestDispatcher dispatcher =
            getServletContext()
                .getRequestDispatcher("/banner");

        if (dispatcher != null) {
            dispatcher.include(request, response);
        }

        // Print out the total and the form for the user
        out.println("<p>" + messages.getString("Amount") + "<strong>" +
            c.getFormat() + "</strong>" + "<p>" +
            messages.getString("Purchase") + "<form action=\"" +
            response.encodeURL(request.getContextPath() + "/bookreceipt") +
            "\" method=\"post\">" + "<table summary=\"layout\">" + "<tr>" +
            "<td><strong>" + messages.getString("Name") + "</strong></td>" +
            "<td><input type=\"text\" name=\"cardname\"" +
            "value=\"Gwen Canigetit\" size=\"19\"></td>" + "</tr>" + "<tr>" +
            "<td><strong>" + messages.getString("CCNumber") + "</strong></td>" +
            "<td>" + "<input type=\"text\" name=\"cardnum\" " +
            "value=\"xxxx xxxx xxxx xxxx\" size=\"19\"></td>" + "</tr>" +
            "<tr>" + "<td></td>" + "<td><input type=\"submit\"" + "value=\"" +
            messages.getString("Submit") + "\"></td>" + "</tr>" + "</table>" +
            "</form>" + "</body>" + "</html>");
        out.close();
    }

    public String getServletInfo() {
        return "The Cashier servlet takes the user's name and " +
        "credit card number so that the user can buy the books.";
    }
}
