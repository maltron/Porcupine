drop database if exists RESOURCE_SERVER;
create database if not exists RESOURCE_SERVER character set utf8 collate utf8_unicode_ci;
use RESOURCE_SERVER;
create table if not exists RS_ROLE(ROLE_ID bigint not null auto_increment, ROLENAME varchar(30), primary key(ROLE_ID), unique(ROLENAME)) Engine=InnoDB;
create table if not exists RS_USER(USER_ID bigint not null auto_increment, EMAIL varchar(70) not null, PASSWORD varchar(255) not null, ROLE_ID bigint null,  
primary key(USER_ID), unique(EMAIL), constraint USER_BELONGS_TO_ROLE foreign key(ROLE_ID) references RS_ROLE(ROLE_ID)) Engine=InnoDB;
insert into RS_ROLE(ROLENAME) values('admin');
insert into RS_ROLE(ROLENAME) values('user');
insert into RS_USER(EMAIL, PASSWORD, ROLE_ID) values('admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 1);

