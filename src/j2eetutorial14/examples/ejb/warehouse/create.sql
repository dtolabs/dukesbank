// Create tables for Warehouse application

CREATE TABLE INVENTORY (
    product_id varchar(3),  
    quantity decimal(10));

INSERT INTO INVENTORY  
values ('123', 100);

CREATE TABLE ORDER_ITEM (
    order_id varchar(3),  
    product_id varchar(3),  
    status varchar(8));

INSERT INTO ORDER_ITEM  
values ('456', '123', 'open');
