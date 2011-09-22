// Delete tables for Teller application

ALTER TABLE CHECKING DROP CONSTRAINT pk_checking;

DROP TABLE CHECKING;
DROP TABLE CASH_IN_MACHINE;
