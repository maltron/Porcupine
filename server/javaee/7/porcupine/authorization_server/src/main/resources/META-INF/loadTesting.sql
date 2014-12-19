# Roles
insert into PORCUPINE_ROLE(ROLENAME) values('God');
insert into PORCUPINE_ROLE(ROLENAME) values('Admin');
insert into PORCUPINE_ROLE(ROLENAME) values('User');
# Users
insert into PORCUPINE_USER(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, ROLE_ID) values('god', 'god', 'god', 'VyM2DvEQQ6h5UgQS6a2Jfg68uZzIIOw2O/7MnXUaGpk=', 1);
insert into PORCUPINE_USER(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, ROLE_ID) values('admin', 'admin', 'admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 2);
insert into PORCUPINE_USER(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, PHONE_TYPE, AREA_CODE, NUMBER, ROLE_ID) values('maltron@gmail.com', 'Mauricio', 'Leal', '0eS2q/1F+/jhnShNKsI/JfT8L9B17tDPWV1Aze4R610=', 'MOBILE', '11', '9 8578 0945', 3);
# Administrators
insert into PORCUPINE_ADMINISTRATOR(EMAIL, NAME, PHONE_TYPE, AREA_CODE, NUMBER) values('valeria@gmail.com', 'Valeria Oliveira', 'MOBILE', '91', '8944 3344');
insert into PORCUPINE_ADMINISTRATOR(EMAIL, NAME, PHONE_TYPE, AREA_CODE, NUMBER) values('ealeal@gmail.com', 'Eduardo Leal', 'WORK', '21', '8933 2122');
insert into PORCUPINE_ADMINISTRATOR(EMAIL, NAME, PHONE_TYPE, AREA_CODE, NUMBER) values('fernando@gmail.com', 'Fernando Silveira', 'MAIN', '41', '2213 3223');
# Permission
insert into PORCUPINE_PERMISSION(LOGIC) values("ANYONE");
insert into PORCUPINE_PERMISSION(LOGIC) values("OWNER");
insert into PORCUPINE_PERMISSION(LOGIC) values("ROLE");
# Scopes
insert into PORCUPINE_SCOPE(NAME, DESCRIPTION, MESSAGE, IS_AUTHORIZATION_CODE_GRANT, IS_IMPLICIT_GRANT, IS_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT, IS_CLIENT_CREDENTIALS_GRANT, EXPIRATION, USERNAME, PASSWORD) values('EMAIL', 'Personal Email Information', 'The application Sample App by Aaron Parecki would like the ability to access your personal E-Mail information', true, true, true, true, 300000, 'maltron@gmail.com', '1234567890');
insert into PORCUPINE_SCOPE(NAME, DESCRIPTION, MESSAGE, IS_AUTHORIZATION_CODE_GRANT, IS_IMPLICIT_GRANT, IS_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT, IS_CLIENT_CREDENTIALS_GRANT, EXPIRATION) values('PHOTOS', 'Personal Photos', 'The application Sample App by Aaron Parecki would like the ability to access your Photos', true, false, false, false, 300000);
insert into PORCUPINE_SCOPE(NAME, DESCRIPTION, MESSAGE, IS_AUTHORIZATION_CODE_GRANT, IS_IMPLICIT_GRANT, IS_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT, IS_CLIENT_CREDENTIALS_GRANT, EXPIRATION) values('SCOPE_AC', 'Testing for Authorization Code', 'The test application by Mauricio \"Maltron\" Leal would like the ability to access a sample email', true, false, false, false, 300000);
# Protected Resources
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/client/rest/person/implicit');
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/client/rest/person/authorization');
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/client/rest/person/resourceownerpasswordcredentials');
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/client/rest/person/clientcredentials');
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/rest/personal/information/photos');
insert into PORCUPINE_PROTECTED_RESOURCE(RESOURCE) values('/testac/rest/resource');
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(1,1);
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(1,2);
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(1,3);
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(1,4);
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(2,5);
insert into PORCUPINE_SCOPE_ACCESS_PROTECTED_RESOURCES(SCOPE_ID, PROTECTED_RESOURCE_ID) values(3,6);
# Client
insert into PORCUPINE_CLIENT(ENABLED, CLIENT_ID, SECRET, NAME, DESCRIPTION) values(true, 'cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=', 'cG9yY3VwaW5lLWNsaWVudC1zZWNyZXQtYjRmM2QyZmYtYmUxZS00ZjkxLWFiZWQtMjAyYWU1MTRhMDgy', 'Sample App', 'A simple example on how an apllication using OAuth 2.0 should behaviour');
insert into PORCUPINE_CLIENT_HAS_SCOPES(CLIENT_ID, SCOPE_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=',1);
insert into PORCUPINE_CLIENT_HAS_SCOPES(CLIENT_ID, SCOPE_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=',2);
insert into PORCUPINE_CLIENT_HAS_SCOPES(CLIENT_ID, SCOPE_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=',3);
# Administrators for this Client
insert into PORCUPINE_CLIENT_HAS_ADMINS(CLIENT_ID, ADMINISTRATOR_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=', 1);
insert into PORCUPINE_CLIENT_HAS_ADMINS(CLIENT_ID, ADMINISTRATOR_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=', 2);
insert into PORCUPINE_CLIENT_HAS_ADMINS(CLIENT_ID, ADMINISTRATOR_ID) values('cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=', 3);
