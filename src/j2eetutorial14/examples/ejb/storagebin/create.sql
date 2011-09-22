// Create tables for Storage Bin application

CREATE TABLE WIDGET (
    widgetid varchar(3) constraint pk_widget primary key,
    description varchar(50),
    price decimal (10,2));

INSERT INTO WIDGET
values ('876', 'Intergalactic Transporter', 1300.00);
INSERT INTO WIDGET
values ('543', 'Superwarp Retrodrive', 55.00);
INSERT INTO WIDGET
values ('777', 'Duct Tape', 1.00);

CREATE TABLE STORAGEBIN (
    storagebinid varchar(3) constraint pk_storagebin primary key,  
    widgetid varchar(3),
    quantity integer,
    constraint fk_widgetid foreign key (widgetid) references widget(widgetid));

INSERT INTO STORAGEBIN
values ('123', '876', 100);
INSERT INTO STORAGEBIN
values ('221', '543', 50);
INSERT INTO STORAGEBIN
values ('388', '777', 500);
