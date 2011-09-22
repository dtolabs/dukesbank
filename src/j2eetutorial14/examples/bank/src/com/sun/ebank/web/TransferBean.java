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


public class TransferBean {
    private BigDecimal transferAmount;
    private String fromAccountId;
    private String toAccountId;
    private ResourceBundle messages;
    private BeanManager beanManager;

    public TransferBean() {
        beanManager = null;
        transferAmount = null;
        fromAccountId = null;
        toAccountId = null;
        messages = null;
    }

    public String doTx() {
        String message = null;
        TxController txCtl = beanManager.getTxController();

        try {
            txCtl.transferFunds(transferAmount, "Transfer", fromAccountId,
                toAccountId);
        } catch (RemoteException e) {
            message = e.getMessage();
            Debug.print(message);
        } catch (InvalidParameterException e) {
            // Not possible
        } catch (AccountNotFoundException e) {
            // Not possible
        } catch (InsufficientFundsException e) {
            message = messages.getString("InsufficientFundsError");
            Debug.print(message);
        } catch (InsufficientCreditException e) {
            message = messages.getString("InsufficientCreditError");
            Debug.print(message);
        }

        return message;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
        Debug.print("Setting transfer amount to: " + transferAmount);
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
        Debug.print("Setting from account id to: " + fromAccountId);
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
        Debug.print("Setting to account id to: " + toAccountId);
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }
}
