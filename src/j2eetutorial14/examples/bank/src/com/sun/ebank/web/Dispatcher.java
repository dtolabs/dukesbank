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


package com.sun.ebank.web;

import javax.servlet.*;
import javax.servlet.http.*;
import com.sun.ebank.util.Debug;
import java.util.*;
import java.math.BigDecimal;


public class Dispatcher extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        BeanManager beanManager =
            (BeanManager) session.getAttribute("beanManager");

        if (beanManager == null) {
            Debug.print("Creating bean manager.");
            beanManager = new BeanManager();
            session.setAttribute("beanManager", beanManager);
        }

        String selectedScreen = request.getServletPath();

        Debug.print(selectedScreen);

        if (selectedScreen.equals("/accountHist")) {
            AccountHistoryBean accountHistoryBean = new AccountHistoryBean();
            request.setAttribute("accountHistoryBean", accountHistoryBean);

            try {
                accountHistoryBean.setBeanManager(beanManager);
                accountHistoryBean.setAccountId(request.getParameter(
                        "accountId"));
                accountHistoryBean.setSortOption(Integer.parseInt(
                        request.getParameter("sortOption")));
                accountHistoryBean.setActivityView(Integer.parseInt(
                        request.getParameter("activityView")));
                accountHistoryBean.setDate(Integer.parseInt(
                        request.getParameter("date")));
                accountHistoryBean.setYear(Integer.parseInt(
                        request.getParameter("year")));
                accountHistoryBean.setSinceDay(Integer.parseInt(
                        request.getParameter("sinceDay")));
                accountHistoryBean.setSinceMonth(Integer.parseInt(
                        request.getParameter("sinceMonth")));
                accountHistoryBean.setBeginDay(Integer.parseInt(
                        request.getParameter("beginDay")));
                accountHistoryBean.setBeginMonth(Integer.parseInt(
                        request.getParameter("beginMonth")));
                accountHistoryBean.setEndDay(Integer.parseInt(
                        request.getParameter("endDay")));
                accountHistoryBean.setEndMonth(Integer.parseInt(
                        request.getParameter("endMonth")));
            } catch (NumberFormatException e) {
            }

            accountHistoryBean.doTx();
        }

        try {
            Debug.print("Forwarding to template.");
            request.getRequestDispatcher("/template/template.jsp")
                   .forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ResourceBundle messages =
            (ResourceBundle) session.getAttribute("messages");

        if (messages == null) {
            Locale locale = request.getLocale();
            Debug.print(locale.getDisplayName());
            messages = ResourceBundle.getBundle("WebMessages", locale);

            if (messages == null) {
                Debug.print("Didn't locate resource bundle.");
            }

            session.setAttribute("messages", messages);
        }

        String selectedScreen = request.getServletPath();
        Debug.print(selectedScreen);

        BeanManager beanManager =
            (BeanManager) session.getAttribute("beanManager");

        if (beanManager == null) {
            beanManager = new BeanManager();
            session.setAttribute("beanManager", beanManager);
        }

        if (selectedScreen.equals("/accountHist")) {
            AccountHistoryBean accountHistoryBean = new AccountHistoryBean();
            request.setAttribute("accountHistoryBean", accountHistoryBean);

            try {
                accountHistoryBean.setBeanManager(beanManager);
                accountHistoryBean.setAccountId(request.getParameter(
                        "accountId"));
                accountHistoryBean.setSortOption(Integer.parseInt(
                        request.getParameter("sortOption")));
                accountHistoryBean.setActivityView(Integer.parseInt(
                        request.getParameter("activityView")));
                accountHistoryBean.setYear(Integer.parseInt(
                        request.getParameter("year")));
                accountHistoryBean.setDate(Integer.parseInt(
                        request.getParameter("date")));
                accountHistoryBean.setSinceDay(Integer.parseInt(
                        request.getParameter("sinceDay")));
                accountHistoryBean.setSinceMonth(Integer.parseInt(
                        request.getParameter("sinceMonth")));
                accountHistoryBean.setBeginDay(Integer.parseInt(
                        request.getParameter("beginDay")));
                accountHistoryBean.setBeginMonth(Integer.parseInt(
                        request.getParameter("beginMonth")));
                accountHistoryBean.setEndDay(Integer.parseInt(
                        request.getParameter("endDay")));
                accountHistoryBean.setEndMonth(Integer.parseInt(
                        request.getParameter("endMonth")));
            } catch (NumberFormatException e) {
                // Not possible
            }

            accountHistoryBean.doTx();
        } else if (selectedScreen.equals("/transferAck")) {
            String fromAccountId = request.getParameter("fromAccountId");
            String toAccountId = request.getParameter("toAccountId");

            if ((fromAccountId == null) || (toAccountId == null)) {
                request.setAttribute("errorMessage",
                    messages.getString("AccountError"));

                try {
                    request.getRequestDispatcher("/error.jsp")
                           .forward(request, response);
                } catch (Exception ex) {
                }
            } else {
                TransferBean transferBean = new TransferBean();
                request.setAttribute("transferBean", transferBean);

                try {
                    transferBean.setMessages(messages);
                    transferBean.setBeanManager(beanManager);
                    transferBean.setFromAccountId(fromAccountId);
                    transferBean.setToAccountId(toAccountId);
                    transferBean.setTransferAmount((new BigDecimal(
                            request.getParameter("transferAmount"))).setScale(2));

                    String errorMessage = transferBean.doTx();

                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);

                        try {
                            request.getRequestDispatcher("/error.jsp")
                                   .forward(request, response);
                        } catch (Exception ex) {
                        }
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage",
                        messages.getString("AmountError"));

                    try {
                        request.getRequestDispatcher("/error.jsp")
                               .forward(request, response);
                    } catch (Exception ex) {
                    }
                }
            }
        } else if (selectedScreen.equals("/atmAck")) {
            ATMBean atmBean = new ATMBean();
            request.setAttribute("atmBean", atmBean);

            try {
                atmBean.setMessages(messages);
                atmBean.setBeanManager(beanManager);
                atmBean.setAccountId(request.getParameter("accountId"));
                atmBean.setAmount((new BigDecimal(request.getParameter("amount"))).setScale(
                        2));
                atmBean.setOperation(Integer.parseInt(request.getParameter(
                            "operation")));

                String errorMessage = atmBean.doTx();

                if (errorMessage != null) {
                    request.setAttribute("errorMessage", errorMessage);

                    try {
                        request.getRequestDispatcher("/error.jsp")
                               .forward(request, response);
                    } catch (Exception ex) {
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage",
                    messages.getString("AmountError"));

                try {
                    request.getRequestDispatcher("/error.jsp")
                           .forward(request, response);
                } catch (Exception ex) {
                }
            }
        }

        try {
            request.getRequestDispatcher("/template/template.jsp")
                   .forward(request, response);
        } catch (Exception ex) {
        }
    }
}
