-- create tables for online banking app
-- also seeds next_id table w. initial values

DROP TABLE customer_account_xref if exists;
DROP TABLE tx if exists;
DROP TABLE customer if exists;
DROP TABLE account if exists;
DROP TABLE next_id if exists;

CREATE TABLE account(
    account_id VARCHAR(8),
    type VARCHAR(24),
    description VARCHAR(30),
    balance NUMERIC(10,2),
    credit_line NUMERIC(10,2),
    begin_balance NUMERIC(10,2),
    begin_balance_time_stamp TIMESTAMP,
    CONSTRAINT pk_account PRIMARY KEY (account_id));

CREATE TABLE customer(
    customer_id VARCHAR(8),
    last_name VARCHAR(30),
    first_name VARCHAR(30),
    middle_initial VARCHAR(1),
    street VARCHAR(40),
    city VARCHAR(40),
    state VARCHAR(2),
    zip VARCHAR(5),
    phone VARCHAR(16),
    email VARCHAR(30),
    CONSTRAINT pk_customer PRIMARY KEY (customer_id));

CREATE TABLE tx (
    tx_id VARCHAR(8),
    account_id VARCHAR(8),
    time_stamp TIMESTAMP,
    amount NUMERIC(10,2),
    balance NUMERIC(10,2),
    description VARCHAR(30),
    CONSTRAINT pk_tx PRIMARY KEY (tx_id));

CREATE TABLE customer_account_xref(
    customer_id VARCHAR(8), 
    account_id VARCHAR(8));

--    CONSTRAINT fk_customer_id FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
--    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account(account_id))


CREATE TABLE next_id (
    beanName VARCHAR(30),
    id NUMERIC,
    CONSTRAINT pk_nextid PRIMARY KEY (beanName));

