// Create tables for Bank application

CREATE TABLE CHECKING  
(id varchar(3) constraint pk_checking primary key,  
balance decimal(10,2));

INSERT INTO CHECKING  
values ('123', 100.00);

CREATE TABLE SAVING  
(id varchar(3) constraint pk_saving primary key,  
balance decimal(10,2));

INSERT INTO SAVING  
values ('123', 500.00);
