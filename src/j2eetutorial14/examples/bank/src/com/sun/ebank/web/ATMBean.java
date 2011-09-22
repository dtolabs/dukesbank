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
import com.sun.ebank.ejb.tx.TxController;
import com.sun.ebank.ejb.exception.*;
import java.util.*;
import java.rmi.RemoteException;
import java.math.BigDecimal;


public class ATMBean {
    private BigDecimal amount;
    private int operation;
    private String accountId;
    private String operationString;
    private String prepString;
    private AccountDetails selectedAccount;
    private ResourceBundle messages;
    private BeanManager beanManager;

    public ATMBean() {
        messages = null;
        beanManager = null;
        amount = null;
        operation = 0;
        accountId = null;
        operationString = null;
        prepString = null;
        selectedAccount = null;
    }

    public String doTx() {
        String message = null;
        TxController txctl = beanManager.getTxController();

        operationString = messages.getString("WithdrewString");
        prepString = messages.getString("FromString");
        Debug.print(accountId);

        beanManager.setAccount(accountId);
        selectedAccount = beanManager.getAccountDetails();

        boolean isCreditAcct = false;

        if (selectedAccount.getType()
                               .equals("Credit")) {
            isCreditAcct = true;
        }

        if (isCreditAcct) {
            if (operation == 0) {
                try {
                    txctl.makeCharge(amount, "ATM Withdrawal", accountId);
                } catch (RemoteException e) {
                    Debug.print(message);

                    return e.getMessage();
                } catch (InvalidParameterException e) {
                    // Not possible
                } catch (AccountNotFoundException e) {
                    // Not possible
                } catch (InsufficientCreditException e) {
                    message = messages.getString("InsufficientCreditError");
                    Debug.print(message);
                } catch (IllegalAccountTypeException e) {
                    // Not possible
                }
            } else {
                operationString = messages.getString("DepositedString");
                prepString = messages.getString("ToString");

                try {
                    txctl.makePayment(amount, "ATM Deposit", accountId);
                } catch (RemoteException e) {
                    Debug.print(message);

                    return e.getMessage();
                } catch (InvalidParameterException e) {
                    // Not possible
                } catch (AccountNotFoundException e) {
                    // Not possible
                } catch (IllegalAccountTypeException e) {
                    // Not possible
                }
            }
        } else {
            if (operation == 0) {
                try {
                    txctl.withdraw(amount, "ATM Withdrawal", accountId);
                } catch (RemoteException e) {
                    Debug.print(message);

                    return e.getMessage();
                } catch (InvalidParameterException e) {
                    // Not possible
                } catch (AccountNotFoundException e) {
                    // Not possible
                } catch (IllegalAccountTypeException e) {
                    // Not possible
                } catch (InsufficientFundsException e) {
                    message = messages.getString("InsufficientFundsError");
                    Debug.print(message);
                }
            } else {
                operationString = messages.getString("DepositedString");
                prepString = messages.getString("ToString");

                try {
                    txctl.deposit(amount, "ATM Deposit", accountId);
                } catch (RemoteException e) {
                    Debug.print(message);

                    return e.getMessage();
                } catch (InvalidParameterException e) {
                    // Not possible
                } catch (AccountNotFoundException e) {
                    // Not possible
                } catch (IllegalAccountTypeException e) {
                    // Not possible
                }
            }
        }

        selectedAccount = beanManager.getAccountDetails();

        return message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOperationString() {
        return operationString;
    }

    public String getPrepString() {
        return prepString;
    }

    public AccountDetails getSelectedAccount() {
        return selectedAccount;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        Debug.print("Setting amount to: " + amount);
    }

    public void setOperation(int operation) {
        this.operation = operation;
        Debug.print("Setting operation to: " + operation);
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
        Debug.print("Setting account id to: " + accountId);
    }

    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }
}
