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


package com.sun.ebank.appclient;

import java.math.BigDecimal;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.ArrayList;
import java.rmi.RemoteException;
import javax.naming.InitialContext;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import com.sun.ebank.ejb.customer.CustomerController;
import com.sun.ebank.ejb.customer.CustomerControllerHome;
import com.sun.ebank.ejb.exception.CustomerNotFoundException;
import com.sun.ebank.ejb.account.AccountController;
import com.sun.ebank.ejb.account.AccountControllerHome;
import com.sun.ebank.ejb.exception.AccountNotFoundException;
import com.sun.ebank.ejb.exception.IllegalAccountTypeException;
import com.sun.ebank.ejb.exception.CustomerInAccountException;
import com.sun.ebank.ejb.exception.InvalidParameterException;
import com.sun.ebank.util.CustomerDetails;
import com.sun.ebank.util.AccountDetails;
import com.sun.ebank.util.EJBGetter;


public class DataModel {
    //Private EJB variables
    private static CustomerController customer;
    private static AccountController account;

    //Private instance variables
    private BankAdmin frame;
    private ResourceBundle messages;
    private int currentFunction;
    private String returned;
    private Date timestamp;

    //Protected instance variables
    protected String first;

    //Protected instance variables
    protected String last;

    //Protected instance variables
    protected String mid;

    //Protected instance variables
    protected String str;

    //Protected instance variables
    protected String cty;

    //Protected instance variables
    protected String st;

    //Protected instance variables
    protected String zp;

    //Protected instance variables
    protected String tel;

    //Protected instance variables
    protected String mail;

    //Protected instance variables
    protected String descrip;

    //Protected instance variables
    protected String credit;

    //Protected instance variables
    protected String type;

    //Protected instance variables
    protected String bal;

    //Protected instance variables
    protected String begbal;

    //Protected instance variables
    protected String custID;

    //Protected instance variables
    protected String actID;
    protected BigDecimal balance;
    protected BigDecimal creditline;
    protected BigDecimal beginbalance;
    protected BigDecimal bigzero = new BigDecimal("0.00");
    protected boolean checkbal;
    protected boolean checkbegbal;

    //Constructor
    public DataModel(BankAdmin frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;

        //Look up and create CustomerController bean
        try {
            CustomerControllerHome customerControllerHome =
                EJBGetter.getCustomerControllerHome();
            customer = customerControllerHome.create();
        } catch (Exception NamingException) {
            NamingException.printStackTrace();
        }

        //Look up and create AccountController bean
        try {
            AccountControllerHome accountControllerHome =
                EJBGetter.getAccountControllerHome();
            account = accountControllerHome.create();
        } catch (Exception NamingException) {
            NamingException.printStackTrace();
        }
    }

    private String getData(JTextField component) {
        String text;
        String trimmed;

        if (component.getText()
                         .length() > 0) {
            text = component.getText();
            trimmed = text.trim();

            return trimmed;
        } else {
            text = null;

            return text;
        }
    }

    protected int checkActData(String returned, int currentFunction) {
        this.currentFunction = currentFunction;
        this.returned = returned;

        if (currentFunction == 6) { //remove account
            this.actID = getData(frame.account);
            this.custID = getData(frame.customer);
            frame.clearMessages(1);

            if ((this.custID != null) && (this.actID != null)) {
                int success = writeData();

                return success;
            } else {
                frame.messlab5.setText(messages.getString(
                        "MissingRequiredException"));

                return 1;
            }
        } else { // create account
                 //Retrieve data from UI
            this.descrip = getData(frame.descrip);
            this.bal = getData(frame.bal);
            this.credit = getData(frame.credit);
            this.begbal = getData(frame.begbal);
            this.custID = getData(frame.cust);

            //Get type
            if (frame.savingsact.isSelected()) {
                this.type = "Savings";
            } else if (frame.checkingact.isSelected()) {
                this.type = "Checking";
            } else if (frame.creditact.isSelected()) {
                this.type = "Credit";
            } else if (frame.mnymktact.isSelected()) {
                this.type = "Money Market";
            } else {
                this.type = null;
            }

            frame.clearMessages(1);

            if (this.begbal != null) {
                checkbegbal = begbal.equals("0");
            }

            //See if user pressed Return after entering
            //beginning balance
            if (this.bal != null) {
                checkbal = bal.equals("0");
            }

            if (checkbal == true) {
                String begbalstring = frame.begbal.getText();
                //Assign beginning balance to balance
                this.bal = begbalstring;
            }

            //Convert balance, begin balance, and credit line
            //String values to BigDecimal types for
            //writing to the database
            balance = new BigDecimal(bal);
            creditline = new BigDecimal(credit);
            beginbalance = new BigDecimal(begbal);

            if ((this.custID != null) &&
                    (this.begbal != null) &&
                    (this.type != null) &&
                    (checkbegbal == false)) {
                int success = writeData();

                return success;
            } else {
                frame.messlab5.setText(messages.getString(
                        "MissingRequiredException"));

                return 1;
            }
        }
    }

    protected int checkCustData(String returned, int currentFunction) {
        this.currentFunction = currentFunction;
        this.returned = returned;

        int i;
        int j;
        int k;

        this.last = getData(frame.lname);
        this.first = getData(frame.fname);
        this.mid = getData(frame.mi);
        this.str = getData(frame.street);
        this.cty = getData(frame.city);
        this.st = getData(frame.state);
        this.zp = getData(frame.zip);
        this.tel = getData(frame.phone);
        this.mail = getData(frame.e);

        frame.clearMessages(1);

        if ((last != null) &&
                (first != null) &&
                (str != null) &&
                (cty != null) &&
                (st != null)) {
            i = 0;
        } else {
            frame.messlab5.setText(messages.getString(
                    "MissingRequiredException"));
            i = 1;
        }

        if (frame.mi.getText()
                        .length() > 1) {
            frame.messlab4.setText(messages.getString("MILimitException"));
            j = 1;
        } else {
            j = 0;
        }

        if (frame.state.getText()
                           .length() > 2) {
            frame.messlab3.setText(messages.getString("StateLimitException"));
            k = 1;
        } else {
            k = 0;
        }

        if ((i == 0) && (j == 0) && (k == 0)) {
            int success = writeData();

            return success;
        } else {
            return 1;
        }
    }

    private int writeData() {
        if (currentFunction == 2) { //Update customer information

            try {
                customer.setName(last, first, mid, returned);
                customer.setAddress(str, cty, st, zp, tel, mail, returned);

                return 0;
            } catch (RemoteException ex) {
                frame.messlab.setText(messages.getString("RemoteException") +
                    ex.getMessage());

                return 1;
            } catch (InvalidParameterException ex) {
                frame.messlab.setText(messages.getString(
                        "InvalidParameterException"));

                return 1;
            } catch (CustomerNotFoundException ex) {
                frame.messlab2.setText(messages.getString("CustomerException") +
                    " " + returned + " " +
                    messages.getString("NotFoundException"));

                return 1;
            }
        }

        if (currentFunction == 1) { //Add new customer information

            try {
                custID = customer.createCustomer(new CustomerDetails(last,
                            first, mid, str, cty, st, zp, tel, mail));

                return 0;
            } catch (InvalidParameterException ex) {
                frame.messlab.setText(messages.getString(
                        "InvalidParameterException"));

                return 1;
            } catch (RemoteException ex) {
                frame.messlab.setText(messages.getString("RemoteException"));

                return 1;
            }
        }

        if (currentFunction == 5) { //Create New Account

            try {
                timestamp = new Date();
                actID = account.createAccount(new AccountDetails(type, descrip,
                            balance, creditline, beginbalance, timestamp),
                        custID);
                System.out.println(actID);

                return 0;
            } catch (InvalidParameterException ex) {
                frame.messlab.setText(messages.getString(
                        "InvalidParameterException"));

                return 1;
            } catch (CustomerNotFoundException ex) {
                frame.messlab2.setText(messages.getString("CustomerException") +
                    " " + custID + " " +
                    messages.getString("NotFoundException"));

                return 1;
            } catch (IllegalAccountTypeException ex) {
                frame.messlab.setText(messages.getString(
                        "IllegalAccountTypeException"));

                return 1;
            } catch (RemoteException ex) {
                frame.messlab.setText(messages.getString("RemoteException") +
                    ex.getMessage());

                return 1;
            }
        }

        if (currentFunction == 6) { //Add Customer to Account

            try {
                account.addCustomerToAccount(custID, actID);

                return 0;
            } catch (CustomerNotFoundException ex) {
                frame.messlab2.setText(messages.getString("CustomerException") +
                    " " + custID + " " +
                    messages.getString("NotFoundException"));

                return 1;
            } catch (AccountNotFoundException ex) {
                frame.messlab2.setText(messages.getString("AccountException") +
                    " " + actID + " " +
                    messages.getString("NotFoundException"));

                return 1;
            } catch (InvalidParameterException ex) {
                frame.messlab.setText(messages.getString(
                        "InvalidParameterException"));

                return 1;
            } catch (RemoteException ex) {
                frame.messlab.setText(messages.getString("RemoteException"));

                return 1;
            }
        }

        return 0;
    }

    protected void removeAccount(String returned) {
        try {
            account.removeAccount(returned);
            frame.messlab2.setText(messages.getString("AccountException") +
                " " + returned + " " + messages.getString("Removed"));
        } catch (AccountNotFoundException ex) {
            frame.messlab2.setText(messages.getString("AccountException") +
                " " + returned + " " + messages.getString("NotFoundException"));
        } catch (InvalidParameterException ex) {
            frame.messlab.setText(messages.getString(
                    "InvalidParameterException"));
        } catch (RemoteException ex) {
            frame.messlab.setText(messages.getString("RemoteException") +
                ex.getMessage());
        }
    }

    protected void searchByLastName(String returned) {
        try {
            ArrayList list = customer.getCustomersOfLastName(returned);

            if (!list.isEmpty()) {
                String custID = ((CustomerDetails) list.get(0)).getCustomerId();
                JOptionPane.showMessageDialog(frame, custID, "Customer ID is:",
                    JOptionPane.PLAIN_MESSAGE);
            } else {
                frame.messlab.setText(returned + " " +
                    messages.getString("NotFoundException"));
            }
        } catch (InvalidParameterException ex) {
            frame.messlab.setText("InvalidParameterException");
        } catch (RemoteException ex) {
            frame.messlab.setText("RemoteException" + ex.getMessage());
        }
    }

    protected void createActInf(int currentFunction, String returned) {
        AccountDetails details = null;

        //View Account Information
        if ((currentFunction == 4) && (returned.length() > 0)) {
            try {
                details = account.getDetails(returned);

                boolean readonly = true;
                frame.setDescription(details.getDescription());

                ArrayList alist = new ArrayList();
                alist = account.getCustomerIds(returned);
                frame.createActFields(readonly, details.getType(),
                    details.getBalance(), details.getCreditLine(),
                    details.getBeginBalance(), alist,
                    details.getBeginBalanceTimeStamp());
            } catch (InvalidParameterException ex) {
                frame.messlab.setText(messages.getString(
                        "InvalidParameterException"));
            } catch (AccountNotFoundException ex) {
                frame.resetPanelTwo();
                frame.messlab2.setText(messages.getString("AccountException") +
                    " " + returned + " " +
                    messages.getString("NotFoundException"));
            } catch (RemoteException ex) {
                frame.messlab.setText("Remote Exception" + ex.getMessage());
            }
        }

        //Create Account Information
        if (currentFunction == 5) {
            timestamp = new Date();
            frame.setDescription(null);

            boolean readonly = false;
            ArrayList alist = new ArrayList();
            frame.createActFields(readonly, null, bigzero, bigzero, bigzero,
                alist, timestamp);
        }
    }

    protected void createCustInf(int currentFunction, String returned) {
        CustomerDetails details = null;

        //View Customer Information
        if ((currentFunction == 3) && (returned.length() > 0)) {
            try {
                details = customer.getDetails(returned);

                boolean readonly = true;
                frame.createCustFields(true, details.getFirstName(),
                    details.getLastName(), details.getMiddleInitial(),
                    details.getStreet(), details.getCity(), details.getState(),
                    details.getZip(), details.getPhone(), details.getEmail());
            } catch (InvalidParameterException ex) {
                frame.messlab.setText("InvalidParameterException");
            } catch (CustomerNotFoundException ex) {
                frame.resetPanelTwo();
                frame.messlab2.setText(messages.getString("CustomerException") +
                    " " + returned + " " +
                    messages.getString("NotFoundException"));
            } catch (RemoteException ex) {
                frame.messlab.setText("Remote Exception" + ex.getMessage());
            }
        }

        //Update Customer Information
        if ((currentFunction == 2) && (returned.length() > 0)) {
            try {
                details = customer.getDetails(returned);

                boolean readonly = false;
                frame.createCustFields(false, details.getFirstName(),
                    details.getLastName(), details.getMiddleInitial(),
                    details.getStreet(), details.getCity(), details.getState(),
                    details.getZip(), details.getPhone(), details.getEmail());
            } catch (RemoteException ex) {
                frame.messlab.setText("Remote Exception" + ex.getMessage());
            } catch (InvalidParameterException ex) {
                frame.messlab.setText("InvalidParameterException");
            } catch (CustomerNotFoundException ex) {
                frame.resetPanelTwo();
                frame.messlab2.setText(messages.getString("CustomerException") +
                    " " + returned + " " +
                    messages.getString("NotFoundException"));
            }
        }

        //Create Customer Information
        if (currentFunction == 1) {
            boolean readonly = false;
            frame.createCustFields(false, null, null, null, null, null, null,
                null, null, null);
        }
    }
}
