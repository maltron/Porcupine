drop database if exists PORCUPINE;
create database if not exists PORCUPINE character set utf8 collate utf8_unicode_ci;
use SARUMAN;
create table if not exists ROLE(ROLE_ID bigint not null auto_increment, NAME VARCHAR(20) NOT NULL, UNIQUE(NAME), PRIMARY KEY(ROLE_ID)) engine = InnoDB;
insert into ROLE(NAME) values('admin');
insert into ROLE(NAME) values('user');
create table if not exists USER(USER_ID bigint not null auto_increment, USERNAME VARCHAR(35) NOT NULL, PASSWORD VARCHAR(40) NOT NULL, FULLNAME VARCHAR(150) NOT NULL, ROLE_ID bigint not null default 2, ENABLED tinyint not null default 1, UNIQUE(USERNAME), PRIMARY KEY(USER_ID), CONSTRAINT USER_HAS_ROLE FOREIGN KEY(ROLE_ID) REFERENCES ROLE(ROLE_ID)) engine = InnoDB;
insert into USER(USERNAME, PASSWORD, FULLNAME, ROLE_ID) VALUES('admin', 'admin', 'Administrator', 1);
insert into USER(USERNAME, PASSWORD, FULLNAME) VALUES('maltron@gmail.com', 'maltron', 'Mauricio Leal');
insert into USER(USERNAME, PASSWORD, FULLNAME) VALUES('nadia.ulanova@gmail.com', 'nadia', 'Nadia Ulanova');

create table if not exists COUNTRY(COUNTRY_ID bigint not null auto_increment, NAME VARCHAR(50) NOT NULL, UNIQUE(NAME), PRIMARY KEY(COUNTRY_ID)) engine = InnoDB;
create table if not exists REGION(REGION_ID bigint not null auto_increment, NAME VARCHAR(70) not null, COUNTRY_ID bigint not null default 1, UNIQUE(NAME, COUNTRY_ID), PRIMARY KEY(REGION_ID), CONSTRAINT REGION_FROM_COUNTRY FOREIGN KEY(COUNTRY_ID) REFERENCES COUNTRY(COUNTRY_ID)) engine = InnoDB;
create table if not exists CITY(CITY_ID bigint not null auto_increment, NAME VARCHAR(100) not null, REGION_ID bigint not null, UNIQUE(NAME, REGION_ID), PRIMARY KEY(CITY_ID), CONSTRAINT CITY_FROM_REGION FOREIGN KEY(REGION_ID) REFERENCES REGION(REGION_ID)) engine = InnoDB;

insert into COUNTRY(NAME) values('Brazil');
insert into REGION(NAME, COUNTRY_ID) values('São Paulo', 1);
insert into REGION(NAME, COUNTRY_ID) values('Rio de Janeiro', 1);
insert into REGION(NAME, COUNTRY_ID) values('Minas Gerais', 1);
insert into REGION(NAME, COUNTRY_ID) values('Espírito Santo', 1);
insert into REGION(NAME, COUNTRY_ID) values('Paraná', 1);
insert into REGION(NAME, COUNTRY_ID) values('Santa Catarina', 1);
insert into REGION(NAME, COUNTRY_ID) values('Rio Grande do Sul', 1);
insert into REGION(NAME, COUNTRY_ID) values('Bahia', 1);
insert into REGION(NAME, COUNTRY_ID) values('Alagoas', 1);
insert into REGION(NAME, COUNTRY_ID) values('Sergipe', 1);
insert into REGION(NAME, COUNTRY_ID) values('Pernambuco', 1);
insert into REGION(NAME, COUNTRY_ID) values('Paraíba', 1);
insert into REGION(NAME, COUNTRY_ID) values('Rio Grande do Norte', 1);
insert into REGION(NAME, COUNTRY_ID) values('Ceará', 1);
insert into REGION(NAME, COUNTRY_ID) values('Piauí', 1);
insert into REGION(NAME, COUNTRY_ID) values('Maranhão', 1);
insert into REGION(NAME, COUNTRY_ID) values('Tocantins', 1);
insert into REGION(NAME, COUNTRY_ID) values('Goiás', 1);
insert into REGION(NAME, COUNTRY_ID) values('Distrito Federal', 1);
insert into REGION(NAME, COUNTRY_ID) values('Mato Grosso do Sul', 1);
insert into REGION(NAME, COUNTRY_ID) values('Mato Grosso', 1);
insert into REGION(NAME, COUNTRY_ID) values('Rondônia', 1);
insert into REGION(NAME, COUNTRY_ID) values('Acre', 1);
insert into REGION(NAME, COUNTRY_ID) values('Amazonas', 1);
insert into REGION(NAME, COUNTRY_ID) values('Roraima', 1);
insert into REGION(NAME, COUNTRY_ID) values('Amapá', 1);
insert into CITY(NAME, REGION_ID) values('São Paulo', 1);
insert into CITY(NAME, REGION_ID) values('São Bernardo do Campo', 1);
insert into CITY(NAME, REGION_ID) values('Santo André', 1);
insert into CITY(NAME, REGION_ID) values('Diadema', 1);
insert into CITY(NAME, REGION_ID) values('Cotia', 1);
insert into CITY(NAME, REGION_ID) values('Barueri', 1);
insert into CITY(NAME, REGION_ID) values('Santos', 1);
insert into CITY(NAME, REGION_ID) values('Guarujá', 1);
insert into CITY(NAME, REGION_ID) values('Campos de Jordão', 1);
insert into CITY(NAME, REGION_ID) values('Campinas', 1);
insert into CITY(NAME, REGION_ID) values('São José dos Campos', 1);
insert into CITY(NAME, REGION_ID) values('Ribeirão Preto', 1);

create table if not exists PARTNER(PARTNER_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(PARTNER_ID)) engine=InnoDB;
insert into PARTNER(NAME) values('System Manager');
insert into PARTNER(NAME) values('TechniSys');

create table if not exists CUSTOMER(CUSTOMER_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(CUSTOMER_ID)) engine=InnoDB;
insert into CUSTOMER(NAME) values('Bradesco, Banco');
insert into CUSTOMER(NAME) values('Abril, Editora');
insert into CUSTOMER(NAME) values('Oi, Operadora de Telefonia Móvel');
insert into CUSTOMER(NAME) values('EMBRAER');

create table if not exists TECHNOLOGY(TECHNOLOGY_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(TECHNOLOGY_ID)) engine=InnoDB;
insert into TECHNOLOGY(NAME) values('JBoss EAP');
insert into TECHNOLOGY(NAME) values('JBoss BRMS');
insert into TECHNOLOGY(NAME) values('JBoss DataGrid');
insert into TECHNOLOGY(NAME) values('OpenShift');
insert into TECHNOLOGY(NAME) values('OpenStack');

create table if not exists PRESALES(PRESALES_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(PRESALES_ID)) engine=InnoDB;
insert into PRESALES(NAME) values('Preso Development');
insert into PRESALES(NAME) values('Meeting');
insert into PRESALES(NAME) values('Proposal Development');
insert into PRESALES(NAME) values('Training');

create table if not exists POSTSALES(POSTSALES_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(POSTSALES_ID)) engine=InnoDB;
insert into POSTSALES(NAME) values('Consulting');
insert into POSTSALES(NAME) values('Assessment');

create table if not exists BACKOFFICE(BACKOFFICE_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(BACKOFFICE_ID)) engine=InnoDB;
insert into BACKOFFICE(NAME) values('Expense Report');
insert into BACKOFFICE(NAME) values('SalesForce');
insert into BACKOFFICE(NAME) values('Monthly Anniversary');

create table if not exists PTO(PTO_ID bigint not null auto_increment, NAME VARCHAR(100) not null, UNIQUE(NAME), PRIMARY KEY(PTO_ID)) engine=InnoDB;
insert into PTO(NAME) values('Vacation');
insert into PTO(NAME) values('Doctor\'s appointment');
insert into PTO(NAME) values('Masturbation');

# A particular Task to a specific User
create table if not exists TASK(TASK_ID bigint not null auto_increment, QUARTER int not null, WEEK int not null, AMOUNT int not null, 
USER_ID bigint not null, PRIMARY KEY(TASK_ID), CONSTRAINT TASK_FOR_A_USER FOREIGN KEY(USER_ID) REFERENCES USER(USER_ID),
CITY_ID bigint null, CONSTRAINT TRAVELLING FOREIGN KEY(CITY_ID) REFERENCES CITY(CITY_ID),
PARTNER_ID bigint null, CONSTRAINT PARTERNING FOREIGN KEY(PARTNER_ID) REFERENCES PARTNER(PARTNER_ID),
CUSTOMER_ID bigint null, CONSTRAINT CUSTOMER_RELATION FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID),
TECHNOLOGY_ID bigint null, CONSTRAINT RESEARCHING_TECHNOLOGY FOREIGN KEY(TECHNOLOGY_ID) REFERENCES TECHNOLOGY(TECHNOLOGY_ID),
PRESALES_ID bigint null, CONSTRAINT PRESALES_ACTIVITIES FOREIGN KEY(PRESALES_ID) REFERENCES PRESALES(PRESALES_ID),
POSTSALES_ID bigint null, CONSTRAINT POSTSALES_ACTIVITIES FOREIGN KEY(POSTSALES_ID) REFERENCES POSTSALES(POSTSALES_ID),
BACKOFFICE_ID bigint null, CONSTRAINT BACKOFFICE_ACTIVITIES FOREIGN KEY(BACKOFFICE_ID) REFERENCES BACKOFFICE(BACKOFFICE_ID),
PTO_ID bigint null, CONSTRAINT PTO_ACTIVITIES FOREIGN KEY(PTO_ID) REFERENCES PTO(PTO_ID)
) engine=InnoDB;
# Q4W11 for UserID = 2
#insert into TASK(QUARTER, WEEK, USER_ID, AMOUNT) values(4, 11, 2, );

#create tale if not exists TASK_WITH_TRAVELING(TASK_ID bigint not null, CITY_ID bigint not null, UNIQUE(TASK_ID, CITY_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_TRAVELLING FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT PLACES_TRAVELLING FOREIGN KEY(CITY_ID) REFERENCES CITY(CITY_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_PARTNERS(TASK_ID bigint not null, PARTNER_ID bigint not null, UNIQUE(TASK_ID, PARTNER_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_PARTNERS FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT PARTNERS_USED_IN_TASK FOREIGN KEY(PARTNER_ID) REFERENCES PARTNER(PARTNER_ID)) engine=InnoDB;
#insert into TASK_WITH_PARTNERS(TASK_ID, PARTNER_ID) values(1, 1);

#create table if not exists TASK_WITH_CUSTOMERS(TASK_ID bigint not null, CUSTOMER_ID bigint not null, UNIQUE(TASK_ID, CUSTOMER_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_CUSTOMERS FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT CUSTOMERS_ENGAGED_TASK FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_RESEARCH(TASK_ID bigint not null, TECHNOLOGY_ID bigint not null, UNIQUE(TASK_ID, TECHNOLOGY_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_RESEARCHING FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT TECHNOLOGIES_USED_FOR_RESEARCHING FOREIGN KEY(TECHNOLOGY_ID) REFERENCES TECHNOLOGY(TECHNOLOGY_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_PRESALES(TASK_ID bigint not null, PRESALES_ID bigint not null, UNIQUE(TASK_ID, PRESALES_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_PRESALES FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT PRESALES_ACTIVITIES_USED FOREIGN KEY(PRESALES_ID) REFERENCES PRESALES(PRESALES_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_POSTSALES(TASK_ID bigint not null, POSTSALES_ID bigint not null, UNIQUE(TASK_ID, POSTSALES_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_POSTSALES FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT POSTSALES_ACTIVITIES_USED FOREIGN KEY(POSTSALES_ID) REFERENCES POSTSALES(POSTSALES_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_BACKOFFICE(TASK_ID bigint not null, BACKOFFICE_ID bigint not null, UNIQUE(TASK_ID, BACKOFFICE_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_BACKOFFICE FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT BACKOFFICE_ACTIVITIES FOREIGN KEY(BACKOFFICE_ID) REFERENCES BACKOFFICE(BACKOFFICE_ID)) engine=InnoDB;

#create table if not exists TASK_WITH_PTO(TASK_ID bigint not null, PTO_ID bigint not null, UNIQUE(TASK_ID, PTO_ID), PRIMARY KEY(TASK_ID), CONSTRAINT TASK_WITH_PTO FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID), CONSTRAINT PTO_ACTIVITIES FOREIGN KEY(PTO_ID) REFERENCES PTO(PTO_ID)) engine=InnoDB;











