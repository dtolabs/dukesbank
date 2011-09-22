// Create tables for Order application

CREATE TABLE BMP_ORDERS (
    orderid varchar(3) constraint pk_bmp_order primary key,  
    customerid varchar(3),
    totalprice decimal(10,2),
    status varchar(10));

INSERT INTO BMP_ORDERS
values ('456', 'c55', 100.00, 'shipped');

CREATE TABLE LINEITEMS (
    itemno integer,
    orderid varchar(3),
    productid varchar(3),
    unitprice decimal(10,2),
    quantity integer,
    constraint fk_orderid foreign key (orderid) references bmp_orders(orderid));

INSERT INTO LINEITEMS
values (1, '456', 'p67', 89.00, 1);
INSERT INTO LINEITEMS
values (2, '456', 'p12', 11.00, 1);
