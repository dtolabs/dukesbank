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


package filters;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import util.Counter;


public final class HitCounterFilter implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        if (filterConfig == null) {
            return;
        }

        HttpServletRequest hr = (HttpServletRequest) request;
        HttpSession session = hr.getSession();
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        if (messages == null) {
            Locale locale = request.getLocale();
            messages = ResourceBundle.getBundle("messages.BookstoreMessages",
                    locale);
            session.setAttribute("messages", messages);
        }

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        Counter counter =
            (Counter) filterConfig.getServletContext()
                                  .getAttribute("hitCounter");
        writer.println();
        writer.println(
            "=======================================================");
        writer.println("The number of hits is: " + counter.incCounter());
        writer.println(
            "=======================================================");

        // Log the resulting string
        writer.flush();
        System.out.println(sw.getBuffer().toString());

        PrintWriter out = response.getWriter();
        CharResponseWrapper wrapper =
            new CharResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);

        CharArrayWriter caw = new CharArrayWriter();
        caw.write(wrapper.toString().substring(0,
                wrapper.toString().indexOf("</body>") - 1));
        caw.write("<p>\n<center>" + messages.getString("Visitor") +
            "<font color='red'>" + counter.getCounter() + "</font></center>");
        caw.write("\n</body></html>");
        response.setContentLength(caw.toString()
                                     .getBytes().length);
        out.write(caw.toString());
        out.close();
    }

    public String toString() {
        if (filterConfig == null) {
            return ("HitCounterFilter()");
        }

        StringBuffer sb = new StringBuffer("HitCounterFilter(");
        sb.append(filterConfig);
        sb.append(")");

        return (sb.toString());
    }
}
