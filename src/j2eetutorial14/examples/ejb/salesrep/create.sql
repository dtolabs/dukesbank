// Create tables for Salesrep application

CREATE TABLE SALESREP (
    salesrepid varchar(3) constraint pk_salesrep primary key,
    name varchar(24));

INSERT INTO SALESREP
values ('876', 'Clyde Webster');
INSERT INTO SALESREP
values ('543', 'Janice Martin');
INSERT INTO SALESREP
values ('777', 'John Johnston');

CREATE TABLE CUSTOMER (
    customerid varchar(3) constraint pk_customer primary key,  
    salesrepid varchar(3),
    name varchar(24),
    constraint fk_salesrepid foreign key (salesrepid) references salesrep(salesrepid));

INSERT INTO CUSTOMER
values ('123', '876', 'Sal Jones');
INSERT INTO CUSTOMER
values ('987', '777', 'Mary Jackson');
INSERT INTO CUSTOMER
values ('221', '543', 'Alice Smith');
INSERT INTO CUSTOMER
values ('388', '543', 'Bill Williamson');
INSERT INTO CUSTOMER
values ('456', '543', 'Joe Smith');
