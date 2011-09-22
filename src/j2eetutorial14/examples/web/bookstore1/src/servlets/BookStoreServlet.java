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
import exception.*;


/**
 * An HTTP Servlet that overrides the service method to return a
 * simple web page.
 */
public class BookStoreServlet extends HttpServlet {
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
        HttpSession session = request.getSession();
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        if (messages == null) {
            Locale locale = request.getLocale();
            messages = ResourceBundle.getBundle("messages.BookstoreMessages",
                    locale);
            session.setAttribute("messages", messages);
        }

        // set content-type header before accessing the Writer
        response.setContentType("text/html");
        response.setBufferSize(8192);

        PrintWriter out = response.getWriter();

        // then write the data of the response
        out.println("<html>" + "<head><title>Duke's Bookstore</title></head>");

        // Get the dispatcher; it gets the banner to the user
        RequestDispatcher dispatcher =
            getServletContext()
                .getRequestDispatcher("/banner");

        if (dispatcher != null) {
            dispatcher.include(request, response);
        }

        try {
            BookDetails bd = bookDB.getBookDetails("203");

            //Left cell -- the "book of choice"
            out.println("<b>" + messages.getString("What") + "</b>" + "<p>" +
                "<blockquote>" + "<em><a href=\"" +
                response.encodeURL(request.getContextPath() +
                    "/bookdetails?bookId=203") + "\">" + bd.getTitle() +
                "</a></em>" + messages.getString("Talk") + "</blockquote>");

            //Right cell -- various navigation options
            out.println("<p><a href=\"" +
                response.encodeURL(request.getContextPath() + "/bookcatalog") +
                "\"><b>" + messages.getString("Start") + "</b></a></font><br>" +
                "<br> &nbsp;" + "<br> &nbsp;" + "<br> &nbsp;" + "</body>" +
                "</html>");
        } catch (BookNotFoundException ex) {
            response.resetBuffer();
            throw new ServletException(ex);
        }

        out.close();
    }

    public String getServletInfo() {
        return "The BookStore servlet returns the main web page " +
        "for Duke's Bookstore.";
    }
}
