package com.jboss.ebank;

import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.ParameterMode;

import javax.xml.namespace.QName;
import java.util.ArrayList;

import java.net.URL;

public class WSClient {
    public static void main(String[] args) 
        throws Exception 
    {
        URL url = 
            new URL("http://localhost:8080/bankws-ejb/Teller?wsdl");

        QName qname = new QName("http://ebank.jboss.com",
                                "TellerService");

        ServiceFactory factory = ServiceFactory.newInstance();
        Service        service = factory.createService(url, qname);

        TellerEndpoint endpoint = (TellerEndpoint)
            service.getPort(TellerEndpoint.class);

        String customer = "200";
        System.out.println("Customer: " + customer);
        
        AccountList accounts = endpoint.getAccountsOfCustomer(customer);
        String[]    ids      = accounts.getAccounts();
        for (int i=0; i<ids.length; i++) {
            System.out.println("account[" + ids[i] +  "]  " + 
                               endpoint.getAccountBalance(ids[i]));
        }
    }

}
