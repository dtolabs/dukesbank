package com.jboss.ebank;

public class AccountList {
    private String[] accounts;

    public AccountList() {
    }

    public AccountList(String[] accounts) {
        setAccounts(accounts);
    }

    public String[] getAccounts() {
        return accounts;
    }
    
    public void setAccounts(String[] accounts) {
        this.accounts = accounts;
    }
}
