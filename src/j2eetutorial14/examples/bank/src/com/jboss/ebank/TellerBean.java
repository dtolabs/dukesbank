package com.jboss.ebank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;

import com.sun.ebank.ejb.account.AccountController;
import com.sun.ebank.ejb.account.AccountControllerHome;
import com.sun.ebank.util.AccountDetails;

public class TellerBean 
    implements SessionBean 
{
    public TellerBean() {
    }

    private AccountController getController() 
	throws RemoteException,
	       NamingException,
	       CreateException
    {
	InitialContext ctx = new InitialContext();
	AccountControllerHome home = (AccountControllerHome)
	    ctx.lookup("java:comp/env/ejb/account");
	
	return home.create();
    }

    public BigDecimal getAccountBalance(String accountID) {
	try {
	    AccountController mgr     = getController();
	    AccountDetails    details = mgr.getDetails(accountID);

	    return details.getBalance();
	} catch (Exception e) {
	    e.printStackTrace();
	    return new BigDecimal(0);
	}
    }

    public AccountList getAccountsOfCustomer(String customerId) 
    {
	try {
	    AccountController mgr = getController();
	
	    ArrayList list    = getController().getAccountsOfCustomer(customerId);

	    String[]  res     = new String[list.size()];
	    Iterator  it_list = list.iterator();
	    for (int i=0; it_list.hasNext(); i++) {
		AccountDetails details = (AccountDetails) it_list.next();
		res[i] = details.getAccountId();
	    }

	    return new AccountList(res);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new AccountList();
	}
    }


    public void ejbCreate() { }
    public void ejbRemove() { }
    
    public void ejbActivate()  { }
    public void ejbPassivate() { }

    public void setSessionContext(SessionContext sc) { }
}
