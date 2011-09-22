package com.jboss.ebank;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.ArrayList;

import java.math.BigDecimal;

public interface TellerEndpoint
    extends Remote
{
    public AccountList getAccountsOfCustomer(String customerId)
	throws RemoteException;

    public BigDecimal getAccountBalance(String accountID)
	throws RemoteException;
}
