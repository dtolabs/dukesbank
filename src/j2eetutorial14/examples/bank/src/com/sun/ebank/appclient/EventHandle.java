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

import java.util.ResourceBundle;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;


public class EventHandle implements ActionListener {
    private static BankAdmin frame;
    private ResourceBundle messages = null;
    private DataModel dataModel;
    private String returned;
    private int currentFunction;

    public EventHandle(BankAdmin frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        this.dataModel = new DataModel(frame, messages);
        //Hook up action events
        hookupEvents();
    }

    //Respond to radio button and return key action events
    public void actionPerformed(ActionEvent event) {
        //Set Account description according to radio buttons
        if (event.getActionCommand() == "savingsstring") {
            frame.setDescription("Savings");
        }

        if (event.getActionCommand() == "checkingstring") {
            frame.setDescription("Checking");
        }

        if (event.getActionCommand() == "creditstring") {
            frame.setDescription("Credit");
        }

        if (event.getActionCommand() == "mnymktstring") {
            frame.setDescription("Money Market");
        }

        //Set balance to beginning balance for new accounts
        //when return key pressed in begining balance field
        Object source = event.getSource();

        if (source == frame.begbal) {
            String begbalstring = frame.begbal.getText();
            frame.bal.setText(begbalstring);
        }
    }

    private void setDisplay() {
        frame.p2.setBackground(Color.white);
        frame.spacerlab1.setText("");
        frame.spacerlab2.setText("");
    }

    private void hookupEvents() {
        //View Customer Information
        frame.viewcust.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String vitem = messages.getString("ViewCust");
                    frame.messlab6.setText(vitem);

                    String mess =
                        new String(messages.getString("EnterCustIDMess"));
                    returned = JOptionPane.showInputDialog(frame, mess);

                    if (returned != null) {
                        currentFunction = 3;
                        dataModel.createCustInf(currentFunction, returned);
                    }
                }
            });

        //Create New Customer
        frame.createcust.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String citem = messages.getString("CreateCust");
                    frame.messlab6.setText(citem);
                    currentFunction = 1;
                    dataModel.createCustInf(currentFunction, returned);
                }
            });

        //Update Customer Information
        frame.updatecust.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String uitem = messages.getString("UpdateCust");
                    frame.messlab6.setText(uitem);

                    String mess =
                        new String(messages.getString("EnterCustIDMess"));
                    returned = JOptionPane.showInputDialog(frame, mess);

                    if (returned != null) {
                        currentFunction = 2;
                        dataModel.createCustInf(currentFunction, returned);
                    }
                }
            });

        //View Account Information
        frame.viewact.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String vitem = messages.getString("ViewAct");
                    frame.messlab6.setText(vitem);

                    String mess =
                        new String(messages.getString("EnterActIDMess"));
                    returned = JOptionPane.showInputDialog(frame, mess);

                    if (returned != null) {
                        currentFunction = 4;
                        dataModel.createActInf(currentFunction, returned);
                    }
                }
            });

        //Create New Account
        frame.createact.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String citem = messages.getString("CreateAct");
                    frame.messlab6.setText(citem);
                    currentFunction = 5;
                    dataModel.createActInf(currentFunction, returned);
                }
            });

        //Add Customer to Account
        frame.addcust.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String aitem = messages.getString("AddCust");
                    frame.messlab6.setText(aitem);
                    currentFunction = 6;
                    frame.addCustToActFields(null, null);
                }
            });

        //Remove Account
        frame.remact.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String ritem = messages.getString("RemAct");
                    frame.messlab6.setText(ritem);

                    String mess =
                        new String(messages.getString("EnterActIDMess"));
                    returned = JOptionPane.showInputDialog(frame, mess);

                    if (returned != null) {
                        currentFunction = 7;
                        dataModel.removeAccount(returned);
                    }
                }
            });

        //Search for Customer ID by last name
        frame.srchcust.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.clearMessages();

                    String sitem = messages.getString("SearchCust");
                    frame.messlab6.setText(sitem);

                    String mess =
                        new String(messages.getString("EnterLastNameMess"));
                    returned = JOptionPane.showInputDialog(frame, mess);

                    if (returned != null) {
                        currentFunction = 8;
                        dataModel.searchByLastName(returned);
                    }
                }
            });

        //Clear data on cancel button press
        frame.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();
                    frame.resetPanelTwo();
                    frame.clearMessages();
                }
            });

        //Process data
        frame.OK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDisplay();

                    if (currentFunction == 3) { //view customer data
                        frame.resetPanelTwo();
                    }

                    if (currentFunction < 3) { //Create or update customer data
                                               //Test data and write to database

                        int complete =
                            dataModel.checkCustData(returned, currentFunction);

                        //if data okay, clear Panel 2
                        if (complete == 0) {
                            if (currentFunction == 1) { //Create New Customer
                                JOptionPane.showMessageDialog(frame,
                                    dataModel.custID, "Customer ID",
                                    JOptionPane.PLAIN_MESSAGE);
                                frame.resetPanelTwo();
                                frame.clearMessages();
                            }

                            if (currentFunction == 2) {
                                frame.resetPanelTwo();
                                frame.clearMessages();
                                frame.messlab2.setText(messages.getString(
                                        "Customer") + " " + returned + " " +
                                    messages.getString("Updated"));
                            }
                        }

                        //If errors, redisplay data to user
                        //and leave error messages on display
                        if (complete == 1) {
                            boolean readonly = false;
                            frame.createCustFields(readonly, dataModel.first,
                                dataModel.last, dataModel.mid, dataModel.str,
                                dataModel.cty, dataModel.st, dataModel.zp,
                                dataModel.tel, dataModel.mail);
                        }
                    }

                    if (currentFunction == 4) { //View account info
                        frame.resetPanelTwo();
                        frame.clearMessages();
                    }

                    if (currentFunction > 4) { //Create account; add cust to act

                        int complete =
                            dataModel.checkActData(returned, currentFunction);

                        if (complete == 0) {
                            if (currentFunction == 5) { //Create New Account
                                                        //Return new account ID
                                frame.resetPanelTwo();
                                frame.clearMessages();
                                JOptionPane.showMessageDialog(frame,
                                    dataModel.actID, "Account ID",
                                    JOptionPane.PLAIN_MESSAGE);
                            }

                            if (currentFunction == 6) { //Add cust to account
                                frame.resetPanelTwo();
                                frame.clearMessages();
                                frame.messlab2.setText(messages.getString(
                                        "Customer") + " " + dataModel.custID +
                                    " " + messages.getString("AddedToAct") +
                                    " " + dataModel.actID);
                            }
                        }

                        if (complete == 1) {
                            if (currentFunction == 5) { //Create New Account
                                frame.setDescription(dataModel.descrip);

                                boolean readonly = false;
                                Date timestamp = new Date();
                                ArrayList alist = new ArrayList();
                                frame.createActFields(readonly, dataModel.type,
                                    dataModel.balance, dataModel.creditline,
                                    dataModel.beginbalance, alist, timestamp);
                                frame.cust.setText(dataModel.custID);
                            } else { //Add Customer to Account
                                frame.addCustToActFields(dataModel.custID,
                                    dataModel.actID);
                            }
                        }
                    }
                }
            });
    }
}
