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
 * This is a simple example of an HTTP Servlet.  It responds to the GET
 * method of the HTTP protocol.
 */
public class BookDetailsServlet extends HttpServlet {
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
        HttpSession session = request.getSession(true);
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        // set headers and buffer size before accessing the Writer
        response.setContentType("text/html");
        response.setBufferSize(8192);

        PrintWriter out = response.getWriter();

        // then write the response
        out.println("<html>" + "<head><title>" +
            messages.getString("TitleBookDescription") + "</title></head>");

        // Get the dispatcher; it gets the banner to the user
        RequestDispatcher dispatcher =
            getServletContext()
                .getRequestDispatcher("/banner");

        if (dispatcher != null) {
            dispatcher.include(request, response);
        }

        //Get the identifier of the book to display
        String bookId = request.getParameter("bookId");

        if (bookId != null) {
            // and the information about the book
            try {
                BookDetails bd = bookDB.getBookDetails(bookId);
                Currency c = (Currency) session.getAttribute("currency");

                if (c == null) {
                    c = new Currency();
                    c.setLocale(request.getLocale());
                    session.setAttribute("currency", c);
                }

                c.setAmount(bd.getPrice());

                //Print out the information obtained
                out.println("<h2>" + bd.getTitle() + "</h2>" + "&nbsp;" +
                    messages.getString("By") + " <em>" + bd.getFirstName() +
                    " " + bd.getSurname() + "</em> &nbsp; &nbsp; " + "(" +
                    bd.getYear() + ")<br> &nbsp; <br>" + "<h4>" +
                    messages.getString("Critics") + "</h4>" + "<blockquote>" +
                    bd.getDescription() + "</blockquote>" + "<h4>" +
                    messages.getString("Price") + c.getFormat() + "</h4>" +
                    "<p><strong><a href=\"" +
                    response.encodeURL(request.getContextPath() +
                        "/bookcatalog?bookId=" + bookId) + "\">" +
                    messages.getString("CartAdd") + "</a>&nbsp;&nbsp;&nbsp;" +
                    "<a href=\"" +
                    response.encodeURL(request.getContextPath() +
                        "/bookcatalog") + "\">" +
                    messages.getString("ContinueShopping") +
                    "</a></p></strong>");
            } catch (BookNotFoundException ex) {
                response.resetBuffer();
                throw new ServletException(ex);
            }
        }

        out.println("</body></html>");
        out.close();
    }

    public String getServletInfo() {
        return "The BookDetail servlet returns information about" +
        "any book that is available from the bookstore.";
    }
}
