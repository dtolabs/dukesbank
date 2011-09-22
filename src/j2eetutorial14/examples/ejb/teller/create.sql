// Create tables for Teller application

CREATE TABLE CHECKING (
    id varchar(3) constraint pk_checking primary key,  
    balance decimal(10,2));

INSERT INTO CHECKING  
values ('123', 500.00);

CREATE TABLE CASH_IN_MACHINE (
    amount decimal(10,2),  
    time_stamp date);

INSERT INTO CASH_IN_MACHINE  
values (10000.00, current_date);
