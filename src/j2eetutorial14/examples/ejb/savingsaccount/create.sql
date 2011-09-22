// Create tables for Savings Account application

CREATE TABLE SAVINGSACCOUNT (
    id varchar(3) constraint pk_savings_account primary key,  
    firstname varchar(24),  
    lastname varchar(24),  
    balance numeric(10,2));
