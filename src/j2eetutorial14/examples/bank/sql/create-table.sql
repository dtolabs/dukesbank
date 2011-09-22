// Create tables for Duke's Bank, the tutorial's online banking application.
// Also seed the NEXT_ID table with initial values.

CREATE TABLE ACCOUNT (
    account_id VARCHAR(8) CONSTRAINT pk_account PRIMARY KEY,
    type VARCHAR(24),
    description VARCHAR(30),
    balance NUMERIC(10,2),
    credit_line NUMERIC(10,2),
    begin_balance NUMERIC(10,2),
    begin_balance_time_stamp TIMESTAMP);

CREATE TABLE CUSTOMER (
    customer_id VARCHAR(8) CONSTRAINT pk_customer PRIMARY KEY,
    last_name VARCHAR(30),
    first_name VARCHAR(30),
    middle_initial VARCHAR(1),
    street VARCHAR(40),
    city VARCHAR(40),
    state VARCHAR(2),
    zip VARCHAR(5),
    phone VARCHAR(16),
    email VARCHAR(30));

CREATE TABLE TX (
    tx_id VARCHAR(8) CONSTRAINT pk_tx PRIMARY KEY,
    account_id VARCHAR(8),
    time_stamp TIMESTAMP,
    amount NUMERIC(10,2),
    balance NUMERIC(10,2),
    description VARCHAR(30),
    CONSTRAINT fk_tx_account_id FOREIGN KEY (account_id) REFERENCES account(account_id));

CREATE TABLE CUSTOMER_ACCOUNT_XREF (
    customer_id VARCHAR(8), 
    account_id VARCHAR(8),
    CONSTRAINT fk_customer_id FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account(account_id));

CREATE TABLE NEXT_ID (
    beanName VARCHAR(30) CONSTRAINT pk_next_id PRIMARY KEY,
    id NUMERIC);

INSERT INTO NEXT_ID
    VALUES ('customer', 202);

INSERT INTO NEXT_ID
    VALUES ('account', 5050);

INSERT INTO NEXT_ID
    VALUES ('tx', 100);

INSERT INTO ACCOUNT VALUES
('5005', 'Money Market', 'Hi Balance', 4000.00, 0.00, 3500.00, '2003-07-28 23:03:20');
 
INSERT INTO ACCOUNT VALUES
('5006', 'Checking', 'Checking', 85.00, 0.00, 66.54, '2003-07-21 03:12:00');

INSERT INTO ACCOUNT VALUES
('5007', 'Credit', 'Visa', 599.18, 5000.00, 166.08, '2003-07-23 10:13:54');

INSERT INTO ACCOUNT VALUES
('5008', 'Savings', 'Super Interest Account', 55601.35, 0.00, 5433.89, '2003-07-15 12:55:33');

INSERT INTO CUSTOMER VALUES
('200', 'Jones', 'Richard', 'K',
 '88 Poplar Ave.', 'Cupertino', 'CA', '95014',
 '408-123-4567', 'rhill@j2ee.com');

INSERT INTO CUSTOMER VALUES
('201', 'Jones', 'Mary', 'R',
 '88 Poplar Ave.', 'Cupertino', 'CA', '95014',
 '408-123-4567', 'mhill@j2ee.com');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('200', '5005');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('201', '5005');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('200', '5006');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('200', '5007');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('201', '5006');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('201', '5007');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('200', '5008');

INSERT INTO CUSTOMER_ACCOUNT_XREF VALUES
('201', '5008');

INSERT INTO TX VALUES
('1', '5005', '2003-9-01 12:55:33', 200.00, 4200.00, 'Refund');
UPDATE ACCOUNT SET balance = 4200.00 WHERE account_id = '5005';
INSERT INTO TX VALUES
('3', '5008', '2003-9-03 12:56:33', -1000.00, 54601.35, 'Transfer Out');
UPDATE ACCOUNT SET balance = 54604.35 WHERE account_id = '5008'; 
INSERT INTO TX VALUES
('4', '5006', '2003-9-03 12:57:33', 1000.00, 1085.00, 'Transfer In');
UPDATE ACCOUNT SET balance = 1085.00 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('5', '5007', '2003-9-05 12:58:33', 33.00, 199.08, 'Clothing');
UPDATE ACCOUNT SET balance = 199.08 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('6', '5006', '2003-9-06 12:59:33', 2000.00, 3085.00, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 3085.00 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('7', '5005', '2003-9-07 13:00:33', -200.00, 4000.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3085.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('8', '5006', '2003-9-08 13:01:33', -200.00, 2885.00, 'Car Insurance');
UPDATE ACCOUNT SET balance = 2885.00 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('9', '5007', '2003-9-09 13:02:33', 186.00, 385.08, 'Car Repair');
UPDATE ACCOUNT SET balance = 385.08 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('10', '5008', '2003-9-10 12:55:33', 1000.00, 55601.35, 'Deposit');
UPDATE ACCOUNT SET balance = 55601.35 WHERE account_id = '5008'; 
INSERT INTO TX VALUES
('11', '5007', '2003-9-11 12:55:33', 585.00, 970.08, 'Airplane Tickets');
UPDATE ACCOUNT SET balance = 970.08 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('12', '5006', '2003-9-12 12:55:33', -675.00, 2210.00, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 2210.00 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('13', '5005', '2003-9-13 12:55:33', -100.00, 3900.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3900.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('14', '5006', '2003-9-14 12:55:33', -385.08, 1824.92, 'Visa Payment');
UPDATE ACCOUNT SET balance = 1824.92 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('15', '5007', '2003-9-15 12:55:33', -385.08, 585.00, 'Payment');
UPDATE ACCOUNT SET balance = 585.00 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('17', '5007', '2003-9-17 12:55:33', 26.95, 611.95, 'Movies');
UPDATE ACCOUNT SET balance = 611.95 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('18', '5006', '2003-9-18 12:55:33', -31.00, 1793.92, 'Groceries');
UPDATE ACCOUNT SET balance = 1793.92 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('19', '5005', '2003-9-19 12:55:33', -150.00, 3750.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3750.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('20', '5006', '2003-9-20 12:55:33', 2000.00, 3173.92, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 3173.92 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('21', '5007', '2003-9-21 12:55:33', 124.00, 735.95, 'Furnishings');
UPDATE ACCOUNT SET balance = 735.95 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('23', '5007', '2003-9-23 12:55:33', 33.12, 769.07, 'Hardware');
UPDATE ACCOUNT SET balance = 769.07 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('24', '5006', '2003-9-24 12:55:33', -175.33, 2998.59, 'Utility Bill');
UPDATE ACCOUNT SET balance = 2998.59 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('25', '5006', '2003-9-25 12:55:33', -123.00, 2875.59, 'Groceries');
UPDATE ACCOUNT SET balance = 2875.59 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('26', '5006', '2003-9-26 12:55:33', -675.00, 2200.59, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 2200.59 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('27', '5007', '2003-9-27 12:55:33', 24.72, 793.79, 'Cafe');
UPDATE ACCOUNT SET balance = 793.79 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('28', '5008', '2003-9-28 12:55:33', 1000.00, 56601.35, 'Deposit');
UPDATE ACCOUNT SET balance = 56601.35 WHERE account_id = '5008'; 
INSERT INTO TX VALUES
('29', '5007', '2003-9-29 12:55:33', 35.00, 828.79, 'Hair Salon');
UPDATE ACCOUNT SET balance = 828.79 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('30', '5006', '2003-9-30 12:55:33', -20.00, 2180.59, 'Gasoline');
UPDATE ACCOUNT SET balance = 2180.59 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('31', '5005', '2003-10-01 12:55:33', -100.00, 3650.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3650.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('32', '5006', '2003-10-02 12:55:33', -56.87, 2123.72, 'Phone Bill');
UPDATE ACCOUNT SET balance = 2123.72 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('33', '5007', '2003-10-03 12:55:33', 67.99, 896.78, 'Acme Shoes');
UPDATE ACCOUNT SET balance = 896.78 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('35', '5007', '2003-10-05 12:55:33', 24.00, 920.78, 'Movies');
UPDATE ACCOUNT SET balance = 920.78 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('36', '5006', '2003-10-06 12:55:33', 2000.00, 4123.72, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 4123.72 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('38', '5006', '2003-10-08 12:55:33', -100.00, 4023.72, 'Groceries');
UPDATE ACCOUNT SET balance = 4023.72 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('39', '5007', '2003-10-09 12:55:33', 26.95, 947.73, 'Pizza');
UPDATE ACCOUNT SET balance = 947.73 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('41', '5007', '2003-10-11 12:55:33', 125.00, 1072.73, 'Dentist');
UPDATE ACCOUNT SET balance = 1072.73 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('42', '5006', '2003-10-12 12:55:33', -675.00, 3348.72, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 3348.72 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('43', '5005', '2003-10-13 12:55:33', -150.00, 3500.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3500.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('44', '5006', '2003-10-14 12:55:33', -947.73, 2400.99, 'Visa Payment');
UPDATE ACCOUNT SET balance = 2400.99 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('45', '5007', '2003-10-15 12:55:33', -947.73, 125.00, 'Payment');
UPDATE ACCOUNT SET balance = 125.00 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('47', '5007', '2003-10-17 12:55:33', 49.90, 100.85, 'Bookstore');
UPDATE ACCOUNT SET balance = 100.85 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('48', '5006', '2003-10-18 12:55:33', -100.00, 2300.99, 'Groceries');
UPDATE ACCOUNT SET balance = 2300.99 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('50', '5006', '2003-10-20 12:55:33', 2000.00, 4300.99, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 4300.99 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('51', '5007', '2003-10-21 12:55:33', 80.32, 181.17, 'Restaurant');
UPDATE ACCOUNT SET balance = 181.17 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('53', '5007', '2003-10-23 12:55:33', 11.78, 192.95, 'Electronics');
UPDATE ACCOUNT SET balance = 192.95 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('54', '5006', '2003-10-24 12:55:33', -150.45, 4150.54, 'Utility Bill');
UPDATE ACCOUNT SET balance = 4150.54 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('55', '5005', '2003-10-25 12:55:33', -100.00, 3400.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3400.00 WHERE account_id = '5005'; 
INSERT INTO TX VALUES
('56', '5006', '2003-10-26 12:55:33', -675.00, 3475.54, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 3475.54 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('57', '5007', '2003-10-27 12:55:33', 24.00, 216.95, 'Ice Skating');
UPDATE ACCOUNT SET balance = 216.95 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('58', '5006', '2003-10-28 12:55:33', -1000.00, 2475.54, 'Transfer Out');
UPDATE ACCOUNT SET balance = 2475.54 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('59', '5008', '2003-10-28 12:55:33', 1000.00, 57601.35, 'Transfer In');
UPDATE ACCOUNT SET balance = 57601.35 WHERE account_id = '5008'; 
INSERT INTO TX VALUES
('60', '5006', '2003-11-02 12:55:33', -99.22, 3376.32, 'Phone Bill');
UPDATE ACCOUNT SET balance = 3376.32 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('61', '5007', '2003-11-03 12:55:33', 29.97, 246.92, 'Toy Store');
UPDATE ACCOUNT SET balance = 246.92 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('62', '5006', '2003-11-04 12:55:33', -2000.00, 376.32, 'Transfer Out');
UPDATE ACCOUNT SET balance = 376.32 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('63', '5008', '2003-11-05 12:55:33', 2000.00, 59601.35, 'Transfer In');
UPDATE ACCOUNT SET balance = 59601.35 WHERE account_id = '5008'; 
INSERT INTO TX VALUES
('64', '5006', '2003-11-06 12:55:33', 2000.00, 2376.32, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 59601.35 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('65', '5007', '2003-11-07 12:55:33', 14.69, 261.61, 'Cafe');
UPDATE ACCOUNT SET balance = 261.61 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('66', '5006', '2003-11-08 12:55:33', -108.99, 2267.33, 'Groceries');
UPDATE ACCOUNT SET balance = 2267.33 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('67', '5006', '2003-11-09 12:55:33', -30.12, 2237.21, 'Gasoline');
UPDATE ACCOUNT SET balance = 2237.21 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('69', '5007', '2003-11-11 12:55:33', 125.00, 386.61, 'Dentist');
UPDATE ACCOUNT SET balance = 2237.21 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('70', '5006', '2003-11-12 12:55:33', -675.00, 1562.21, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 1562.21 WHERE account_id = '5006';  
INSERT INTO TX VALUES
('72', '5006', '2003-11-13 12:55:33', -261.61, 1300.60, 'Visa Payment');
UPDATE ACCOUNT SET balance = 1300.60 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('73', '5007', '2003-11-14 12:55:33', -261.61, 125.00, 'Payment');
UPDATE ACCOUNT SET balance = 125.00 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('75', '5007', '2003-11-15 12:55:33', 24.00, 149.00, 'Drug Store');
UPDATE ACCOUNT SET balance = 149.00 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('76', '5006', '2003-11-16 12:55:33', -67.98, 1232.62, 'Groceries');
UPDATE ACCOUNT SET balance = 1232.62 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('78', '5006', '2003-11-17 12:55:33', 2000.00, 3232.62, 'Paycheck Deposit');
UPDATE ACCOUNT SET balance = 3232.62 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('79', '5007', '2003-11-18 12:55:33', 32.95, 181.95, 'CDs');
UPDATE ACCOUNT SET balance = 181.95 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('81', '5007', '2003-11-20 12:55:33', 14.10, 196.05, 'Sports Store');
UPDATE ACCOUNT SET balance = 196.05 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('82', '5006', '2003-11-21 12:55:33', -99.30, 3133.32, 'Utility Bill');
UPDATE ACCOUNT SET balance = 3133.32 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('84', '5006', '2003-11-21 12:55:33', -675.00, 2458.32, 'Mortgage Payment');
UPDATE ACCOUNT SET balance = 2458.32 WHERE account_id = '5006'; 
INSERT INTO TX VALUES
('85', '5007', '2003-11-22 12:55:33', 23.98, 220.03, 'Garden Supply');
UPDATE ACCOUNT SET balance = 220.03 WHERE account_id = '5007'; 
INSERT INTO TX VALUES
('86', '5005', '2003-11-23 12:55:33', -100.00, 3300.00, 'ATM Withdrawal');
UPDATE ACCOUNT SET balance = 3300.00 WHERE account_id = '5005'; 
