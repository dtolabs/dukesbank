// Create tables for Enroller application

CREATE TABLE STUDENT (
studentid varchar(3) constraint pk_student primary key,  
name varchar(36));

INSERT INTO STUDENT
values ('123', 'Sal Jones');
INSERT INTO STUDENT
values ('221', 'Alice Smith');
INSERT INTO STUDENT
values ('388', 'Elizabeth Willis');
INSERT INTO STUDENT
values ('456', 'Joe Smith');


CREATE TABLE COURSE (
courseid varchar(3) constraint pk_course primary key,  
name varchar(36));

INSERT INTO COURSE
values ('999', 'Advanced Java Programming');
INSERT INTO COURSE
values ('111', 'J2EE for Smart People');
INSERT INTO COURSE
values ('333', 'XML Made Easy');
INSERT INTO COURSE
values ('777', 'An Introduction to Java Programming');

CREATE TABLE ENROLLMENT (
studentid varchar(3),  
courseid varchar(3),  
constraint fk_studentid foreign key (studentid) references student(studentid),
constraint fk_courseid foreign key (courseid) references course(courseid));
