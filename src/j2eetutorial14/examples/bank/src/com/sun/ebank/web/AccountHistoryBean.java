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

import com.sun.ebank.web.BeanManager;
import com.sun.ebank.util.*;
import com.sun.ebank.ejb.account.AccountController;
import com.sun.ebank.ejb.tx.TxController;
import com.sun.ebank.ejb.exception.*;
import java.util.*;
import java.rmi.RemoteException;
import java.math.BigDecimal;


public class AccountHistoryBean {
    private BigDecimal credits;
    private BigDecimal debits;
    private BigDecimal beginningBalance;
    private BigDecimal endingBalance;
    private String accountId;
    private int date;
    private int activityView;
    private int sortOption;
    private int sinceMonth;
    private int sinceDay;
    private int beginMonth;
    private int beginDay;
    private int endMonth;
    private int endDay;
    private int year;
    private ArrayList selectedTransactions;
    private AccountDetails selectedAccount;
    private BeanManager beanManager;

    public AccountHistoryBean() {
        credits = null;
        debits = null;
        beginningBalance = null;
        endingBalance = null;
        selectedAccount = null;
        beanManager = null;
        selectedTransactions = null;
        date = 0;
        activityView = 0;
        sortOption = 0;
        sinceMonth = 1;
        sinceDay = 1;
        beginMonth = 1;
        beginDay = 1;
        endMonth = 12;
        endDay = 31;
        year = 2004;
    }

    public void doTx() {
        BigDecimal amount = new BigDecimal("0.00");
        TxDetails td = null;
        Date startDate = null;
        Date endDate = null;

        try {
            switch (date) {
            case 0:
                startDate = com.sun.ebank.util.DateHelper.getDate(year,
                        sinceMonth, sinceDay);
                endDate = new Date();

                break;

            case 1:
                startDate = com.sun.ebank.util.DateHelper.getDate(year,
                        beginMonth, beginDay);
                endDate = com.sun.ebank.util.DateHelper.getDate(year, endMonth,
                        endDay);

                break;
            }

            ArrayList transactions =
                beanManager.getTxController()
                           .getTxsOfAccount(startDate, endDate, accountId);

            switch (sortOption) {
            case 0:
                Collections.sort(transactions,
                    new Comparator() {
                        public int compare(Object o1, Object o2) {
                            return (((TxDetails) o1).getTimeStamp().compareTo(((TxDetails) o2).getTimeStamp()));
                        }
                    });


                break;

            case 1:
                Collections.sort(transactions,
                    new Comparator() {
                        public int compare(Object o1, Object o2) {
                            return (((TxDetails) o2).getTimeStamp().compareTo(((TxDetails) o1).getTimeStamp()));
                        }
                    });


                break;

            case 2:
                Collections.sort(transactions,
                    new Comparator() {
                        public int compare(Object o1, Object o2) {
                            return (((TxDetails) o1).getDescription().compareTo(((TxDetails) o2).getDescription()));
                        }
                    });


                break;

            case 3:
                Collections.sort(transactions,
                    new Comparator() {
                        public int compare(Object o1, Object o2) {
                            return (((TxDetails) o1).getAmount().compareTo(((TxDetails) o2).getAmount()));
                        }
                    });


                break;
            }

            credits = new BigDecimal("0.00");
            debits = new BigDecimal("0.00");
            beanManager.setAccount(accountId);
            selectedAccount = beanManager.getAccountDetails();
            beginningBalance = selectedAccount.getBalance();
            endingBalance = selectedAccount.getBalance();

            boolean isCreditAcct = false;

            if (selectedAccount.getType()
                                   .equals("Credit")) {
                isCreditAcct = true;
            }

            Iterator i = transactions.iterator();

            if (i.hasNext()) {
                td = (TxDetails) i.next();
                beginningBalance = td.getBalance()
                                     .subtract(td.getAmount());
            }

            i = transactions.iterator();

            if (i.hasNext()) {
                Debug.print("adding to credits and debits.");

                while (i.hasNext()) {
                    td = (TxDetails) i.next();
                    amount = td.getAmount();

                    if (isCreditAcct) {
                        if (amount.floatValue() < 0) {
                            credits = credits.add(amount);
                        } else {
                            debits = debits.subtract(amount);
                        }
                    } else {
                        if (amount.floatValue() > 0) {
                            Debug.print("Adding " + amount + "to credits.");
                            credits = credits.add(amount);
                        } else {
                            Debug.print("Subtracting " + amount +
                                "from debits.");
                            debits = debits.subtract(amount);
                        }
                    }
                }
            }

            if (td != null) {
                endingBalance = td.getBalance();
            }

            selectedTransactions = new ArrayList();
            i = transactions.iterator();

            if (i.hasNext()) {
                switch (activityView) {
                case 0:

                    while (i.hasNext()) {
                        td = (TxDetails) i.next();
                        selectedTransactions.add(td);
                    }

                    break;

                case 1:

                    while (i.hasNext()) {
                        td = (TxDetails) i.next();

                        if (isCreditAcct) {
                            if (td.getAmount()
                                      .floatValue() < 0) {
                                selectedTransactions.add(td);
                            }
                        } else {
                            if (td.getAmount()
                                      .floatValue() > 0) {
                                selectedTransactions.add(td);
                            }
                        }
                    }

                    break;

                case 2:

                    while (i.hasNext()) {
                        td = (TxDetails) i.next();

                        if (isCreditAcct) {
                            if (td.getAmount()
                                      .floatValue() > 0) {
                                selectedTransactions.add(td);
                            }
                        } else {
                            if (td.getAmount()
                                      .floatValue() < 0) {
                                selectedTransactions.add(td);
                            }
                        }
                    }

                    break;
                }
            } else {
                Debug.print("No matching transactions.");
            }
        } catch (InvalidParameterException e) {
        } catch (RemoteException e) {
            Debug.print("Couldn't access enterprise bean: " + e.getMessage());
        }
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public BigDecimal getDebits() {
        return debits;
    }

    public BigDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public BigDecimal getEndingBalance() {
        return endingBalance;
    }

    public Collection getTransactions() {
        return selectedTransactions;
    }

    public AccountDetails getSelectedAccount() {
        return selectedAccount;
    }

    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setBeginMonth(int beginMonth) {
        this.beginMonth = beginMonth;
    }

    public void setBeginDay(int beginDay) {
        this.beginDay = beginDay;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setSinceMonth(int sinceMonth) {
        this.sinceMonth = sinceMonth;
    }

    public void setSinceDay(int sinceDay) {
        this.sinceDay = sinceDay;
    }

    public void setActivityView(int activityView) {
        this.activityView = activityView;
    }

    public void setSortOption(int sortOption) {
        this.sortOption = sortOption;
    }
}
