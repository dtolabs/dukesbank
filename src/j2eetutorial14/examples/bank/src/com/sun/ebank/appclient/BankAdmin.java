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

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;


public class BankAdmin extends JFrame {
    //Protected instance variables
    protected static BankAdmin frame;
    protected static EventHandle ehandle;

    //Internationalization
    private static Locale currentLocale = null;
    protected static ResourceBundle messages = null;
    protected String desc;
    protected JButton OK;
    protected JButton cancel;
    protected JPanel p1;
    protected JPanel p2;
    protected JLabel fnamelab;
    protected JLabel lnamelab;
    protected JLabel milab;
    protected JLabel streetlab;
    protected JLabel citylab;
    protected JLabel statelab;
    protected JLabel ziplab;
    protected JLabel phonelab;
    protected JLabel emaillab;
    protected JLabel spacerlab1;
    protected JLabel spacerlab2;
    protected JLabel messlab;
    protected JLabel messlab2;
    protected JLabel messlab3;
    protected JLabel messlab4;
    protected JLabel messlab5;
    protected JLabel messlab6;
    protected JLabel descriplab;
    protected JLabel typelab;
    protected JLabel balab;
    protected JLabel creditlab;
    protected JLabel begbalab;
    protected JLabel customerlab;
    protected JLabel timelab;
    protected JMenuBar mb;
    protected JMenu custmenu;
    protected JMenu actmenu;
    protected JMenuItem createcust;
    protected JMenuItem viewcust;
    protected JMenuItem updatecust;
    protected JMenuItem createact;
    protected JMenuItem viewact;
    protected JMenuItem addcust;
    protected JMenuItem remact;
    protected JMenuItem srchcust;
    protected JRadioButton checkingact;
    protected JRadioButton savingsact;
    protected JRadioButton creditact;
    protected JRadioButton mnymktact;
    protected ButtonGroup group;
    protected JPanel radioPanel;

    //Editable variables
    protected JTextField fname;

    //Editable variables
    protected JTextField lname;

    //Editable variables
    protected JTextField mi;

    //Editable variables
    protected JTextField street;

    //Editable variables
    protected JTextField city;

    //Editable variables
    protected JTextField state;

    //Editable variables
    protected JTextField account;

    //Editable variables
    protected JTextField customer;

    //Editable variables
    protected JTextField zip;

    //Editable variables
    protected JTextField phone;

    //Editable variables
    protected JTextField e;

    //Editable variables
    protected JTextField descrip;

    //Editable variables
    protected JTextField type;

    //Editable variables
    protected JTextField bal;

    //Editable variables
    protected JTextField credit;

    //Editable variables
    protected JTextField begbal;

    //Editable variables
    protected JTextField cust;

    //Editable variables
    protected JTextField time;

    //Constructor
    public BankAdmin(Locale currentLocale) {
        //Internationalization setup
        messages = ResourceBundle.getBundle("appclient.AdminMessages",
                currentLocale);
        //Create initial UI (Panel 1)
        getContentPane()
            .setLayout(new GridLayout(1, 2));
        p1 = new JPanel();
        p1.setLayout(new GridLayout(20, 1));
        p2 = new JPanel();
        p2.setLayout(new GridLayout(0, 2));
        p1.setBackground(Color.white);
        p2.setBackground(Color.gray);
        getContentPane()
            .add(p1);
        getContentPane()
            .add(p2);
        //Build menu bar
        mb = new JMenuBar();
        setJMenuBar(mb);
        //Build Customer menu
        custmenu = new JMenu(messages.getString("CustAdmin"), true);
        custmenu.setMnemonic(KeyEvent.VK_C);
        mb.add(custmenu);
        createcust = new JMenuItem(messages.getString("CreateCust"));
        createcust.setMnemonic(KeyEvent.VK_U);
        viewcust = new JMenuItem(messages.getString("ViewCust"));
        viewcust.setMnemonic(KeyEvent.VK_V);
        updatecust = new JMenuItem(messages.getString("UpdateCust"));
        updatecust.setMnemonic(KeyEvent.VK_D);
        srchcust = new JMenuItem(messages.getString("SearchCust"));
        srchcust.setMnemonic(KeyEvent.VK_S);
        custmenu.add(createcust);
        custmenu.add(viewcust);
        custmenu.add(updatecust);
        custmenu.add(srchcust);
        //Build Account Menu
        actmenu = new JMenu(messages.getString("ActAdmin"), true);
        actmenu.setMnemonic(KeyEvent.VK_A);
        mb.add(actmenu);
        createact = new JMenuItem(messages.getString("CreateAct"));
        createact.setMnemonic(KeyEvent.VK_R);
        addcust = new JMenuItem(messages.getString("AddCust"));
        addcust.setMnemonic(KeyEvent.VK_T);
        viewact = new JMenuItem(messages.getString("ViewAct"));
        viewact.setMnemonic(KeyEvent.VK_I);
        remact = new JMenuItem(messages.getString("RemAct"));
        remact.setMnemonic(KeyEvent.VK_O);
        actmenu.add(createact);
        actmenu.add(addcust);
        actmenu.add(viewact);
        actmenu.add(remact);
        //Create Panel 2 OK and Cancel buttons
        //So EventHandle constructor can add as action listeners
        OK = new JButton(messages.getString("OKButton"));
        cancel = new JButton(messages.getString("CancelButton"));
        //Create message labels
        messlab = new JLabel();
        messlab2 = new JLabel();
        messlab3 = new JLabel();
        messlab4 = new JLabel();
        messlab5 = new JLabel();
        messlab6 = new JLabel();
        //Add components to panels
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());
        p1.add(new JLabel());

        String wmess = messages.getString("WatchMess");
        p1.add(new JLabel(wmess + "     "));
        p1.add(messlab);
        p1.add(messlab2);
        p1.add(messlab3);
        p1.add(messlab4);
        p1.add(messlab5);
        p1.add(messlab6);
        //Create spacer labels
        spacerlab1 = new JLabel("_____________________________");
        spacerlab2 = new JLabel("_____________________________");
        //Add spacer labels to Panel 2 initial screen
        p2.add(spacerlab1);
        p2.add(spacerlab2);
        //Create description text field
        this.descrip = new JTextField();
        //Add functionality to close window
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    System.exit(0);
                }
            });
    }

    protected void clearMessages(int value) {
        messlab.setText(null);
        messlab2.setText(null);
        messlab3.setText(null);
        messlab4.setText(null);
        messlab5.setText(null);
    }

    protected void clearMessages() {
        messlab.setText(null);
        messlab2.setText(null);
        messlab3.setText(null);
        messlab4.setText(null);
        messlab5.setText(null);
        messlab6.setText(null);
    }

    protected void resetPanelTwo() {
        clearMessages(1);
        p2.removeAll();
        p2.validate();
        p2.repaint();
    }

    protected void createPanelTwoActLabels() {
        descriplab = new JLabel(messages.getString("ActDescrip"));
        descriplab.setLabelFor(descrip);
        typelab = new JLabel(messages.getString("Type"));
        typelab.setLabelFor(type);
        balab = new JLabel(messages.getString("Balance"));
        balab.setLabelFor(bal);
        creditlab = new JLabel(messages.getString("Credit"));
        creditlab.setLabelFor(credit);
        begbalab = new JLabel(messages.getString("BegBal"));
        begbalab.setLabelFor(begbal);
        customerlab = new JLabel(messages.getString("Customers"));
        customerlab.setLabelFor(cust);
        timelab = new JLabel(messages.getString("Time"));
        timelab.setLabelFor(time);
    }

    protected void createPanelTwoCustLabels() {
        fnamelab = new JLabel(messages.getString("FnameLab"));
        fnamelab.setLabelFor(fname);
        lnamelab = new JLabel(messages.getString("LnameLab"));
        lnamelab.setLabelFor(lname);
        milab = new JLabel(messages.getString("MiLab"));
        milab.setLabelFor(mi);
        streetlab = new JLabel(messages.getString("StreetLab"));
        streetlab.setLabelFor(street);
        citylab = new JLabel(messages.getString("CityLab"));
        citylab.setLabelFor(city);
        statelab = new JLabel(messages.getString("StateLab"));
        statelab.setLabelFor(state);
        ziplab = new JLabel(messages.getString("ZipLab"));
        ziplab.setLabelFor(zip);
        phonelab = new JLabel(messages.getString("PhoneLab"));
        phonelab.setLabelFor(phone);
        emaillab = new JLabel(messages.getString("EmailLab"));
        emaillab.setLabelFor(e);
    }

    protected void setDescription(String text) {
        this.desc = text;

        if (text != null) {
            this.descrip.setText(this.desc);
        }
    }

    protected void addCustToActFields(String custID, String actID) {
        p2.removeAll();

        JLabel actnolab = new JLabel(messages.getString("ActNoLab"));
        JLabel custnolab = new JLabel(messages.getString("CustNoLab"));

        if (custID != null) {
            customer = new JTextField(custID);
        } else {
            customer = new JTextField();
        }

        if (actID != null) {
            account = new JTextField(actID);
        } else {
            account = new JTextField();
        }

        p2.add(actnolab);
        p2.add(account);
        p2.add(custnolab);
        p2.add(customer);
        p2.add(OK);
        p2.add(cancel);
        p2.validate();
        p2.repaint();
    }

    protected void createCustFields(boolean readonly, String first,
        String last, String mid, String str, String cty, String st, String zp,
        String tel, String mail) {
        p2.removeAll();
        createPanelTwoCustLabels();
        fname = new JTextField(first);
        lname = new JTextField(last);
        mi = new JTextField(mid);
        street = new JTextField(str);
        city = new JTextField(cty);
        state = new JTextField(st);
        zip = new JTextField(zp);
        phone = new JTextField(tel);
        e = new JTextField(mail);

        if (readonly == true) {
            fname.setEditable(false);
            lname.setEditable(false);
            mi.setEditable(false);
            street.setEditable(false);
            city.setEditable(false);
            state.setEditable(false);
            zip.setEditable(false);
            phone.setEditable(false);
            e.setEditable(false);
        }

        p2.add(fnamelab);
        p2.add(fname);
        p2.add(lnamelab);
        p2.add(lname);
        p2.add(milab);
        p2.add(mi);
        p2.add(streetlab);
        p2.add(street);
        p2.add(citylab);
        p2.add(city);
        p2.add(statelab);
        p2.add(state);
        p2.add(ziplab);
        p2.add(zip);
        p2.add(phonelab);
        p2.add(phone);
        p2.add(emaillab);
        p2.add(e);
        p2.add(OK);
        p2.add(cancel);
        p2.validate();
        p2.repaint();
    }

    protected void makeRadioButtons(String type) {
        //Radio Buttons to choose account type
        if (type != null) {
            if (type == "Savings") {
                savingsact.setSelected(true);
            }

            if (type == "Checking") {
                checkingact.setSelected(true);
            }

            if (type == "Credit") {
                creditact.setSelected(true);
            }

            if (type == "Money Market") {
                mnymktact.setSelected(true);
            }
        } else {
            this.savingsact = new JRadioButton(messages.getString("SavingsAct"));
            savingsact.setActionCommand("savingsstring");
            savingsact.setSelected(false);

            this.checkingact = new JRadioButton(messages.getString(
                        "CheckingAct"));
            checkingact.setActionCommand("checkingstring");
            checkingact.setSelected(false);

            this.creditact = new JRadioButton(messages.getString("CreditAct"));
            creditact.setActionCommand("creditstring");
            creditact.setSelected(false);

            this.mnymktact = new JRadioButton(messages.getString("MnyMktAct"));
            mnymktact.setActionCommand("mnymktstring");
            mnymktact.setSelected(false);

            this.group = new ButtonGroup();
        }

        savingsact.addActionListener(ehandle);
        checkingact.addActionListener(ehandle);
        creditact.addActionListener(ehandle);
        mnymktact.addActionListener(ehandle);

        group.add(savingsact);
        group.add(checkingact);
        group.add(creditact);
        group.add(mnymktact);

        radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(0, 1));
        radioPanel.add(this.savingsact);
        radioPanel.add(this.checkingact);
        radioPanel.add(this.creditact);
        radioPanel.add(this.mnymktact);
    }

    protected void createActFields(boolean readonly, String type,
        BigDecimal bal, BigDecimal creditline, BigDecimal begbalance,
        ArrayList alist, Date timestamp) {
        p2.removeAll();
        createPanelTwoActLabels();

        String custIDs = null;
        NumberFormat numFormat = NumberFormat.getNumberInstance(currentLocale);

        if (alist.size() != 0) {
            custIDs = alist.toString();
            this.cust = new JTextField(custIDs);
        } else {
            this.cust = new JTextField();
        }

        this.descrip = new JTextField(this.desc);
        this.type = new JTextField(type);

        //Internationalize date
        String viewtime = DateFormat.getDateInstance()
                                    .format(timestamp);
        this.time = new JTextField(viewtime);

        //Internationalize numbers
        String balstring = numFormat.format(bal.doubleValue());
        this.bal = new JTextField(balstring);

        String creditstring = numFormat.format(creditline.doubleValue());
        this.credit = new JTextField(creditstring);

        String begbalstring = numFormat.format(begbalance.doubleValue());
        this.begbal = new JTextField(begbalstring);
        //Listen for Return action event
        this.begbal.addActionListener(ehandle);

        if (readonly == false) {
            makeRadioButtons(type);
            p2.add(this.typelab);
            p2.add(this.radioPanel);
            //Read-only part of display
            this.bal.setEditable(false);
            this.time.setEditable(false);
        } else if (readonly == true) {
            this.type.setEditable(false);
            p2.add(this.typelab);
            p2.add(this.type);
            this.descrip.setEditable(false);
            this.type.setEditable(false);
            this.credit.setEditable(false);
            this.begbal.setEditable(false);
            this.bal.setEditable(false);
            this.cust.setEditable(false);
            this.time.setEditable(false);
        }

        p2.add(this.descriplab);
        p2.add(this.descrip);
        p2.add(this.timelab);
        p2.add(this.time);
        p2.add(this.balab);
        p2.add(this.bal);
        p2.add(this.creditlab);
        p2.add(this.credit);
        p2.add(this.begbalab);
        p2.add(this.begbal);
        p2.add(this.customerlab);
        p2.add(this.cust);
        p2.add(OK);
        p2.add(cancel);
        p2.validate();
        p2.repaint();
    }

    public static void main(String[] args) {
        String language;
        String country;

        if (args.length == 1) {
            language = new String(args[0]);
            currentLocale = new Locale(language, "");
        } else if (args.length == 2) {
            language = new String(args[0]);
            country = new String(args[1]);
            currentLocale = new Locale(language, country);
        } else {
            currentLocale = Locale.getDefault();
        }

        frame = new BankAdmin(currentLocale);
        frame.setTitle(messages.getString("CustAndAccountAdmin"));

        WindowListener l =
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            };

        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);
        //Create event handling object
        ehandle = new EventHandle(frame, messages);
    }
}
