# Import the correct variables
source wildfly.sh

# Creating the database
mysql --user=${MYSQL_USERNAME} --password=${MYSQL_PASSWORD} < create-database.sql
# Creating a place to reside the MySQL JDBC implementation into WildFly's Module
#DISABLED mkdir -p ${WILDFLY_LOCATION}/modules/com/mysql/main
# Creating a symbolic link into the modules directory
#DISABLED ln -sv ${MYSQL_JDBC_FILE} ${WILDFLY_LOCATION}/modules/com/mysql/main
#DISABLED cd ${WILDFLY_LOCATION}/modules/com/mysql/main
# Creating all the necessary DataSources
#     STEP #1: Creating the Module
${WILDFLY_LOCATION}/bin/jboss-cli.sh --connect --controller=localhost:9990 --command="module add --name=com.mysql --resources=${MYSQL_JDBC_FILE} --dependencies=javax.api,javax.transaction.api"
#     STEP #2: Creating the JDBC Driver Available
${WILDFLY_LOCATION}/bin/jboss-cli.sh --connect --controller=localhost:9990 --command="/subsystem=datasources/jdbc-driver=MySQL-JDBC-DRIVER:add(driver-module-name=com.mysql,driver-name=MySQL-JDBC-DRIVER,driver-class-name=com.mysql.jdbc.Driver, driver-xa-datasource-class-name=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource)"
#     STEP #3: Creating the DataSource and linking to JDBC Driver
${WILDFLY_LOCATION}/bin/jboss-cli.sh --connect --controller=localhost:9990 --command="/subsystem=datasources/data-source=SarumanDS:add(driver-name=MySQL-JDBC-DRIVER, jndi-name=java:/jdbc/Saruman, connection-url=jdbc:mysql://localhost:3306/SARUMAN, user-name=${MYSQL_USERNAME}, password=${MYSQL_PASSWORD})"

# Creating JMS for Notification
#asadmin create-jms-resource --restype javax.jms.Queue jms/QueueNotification
#asadmin create-jms-resource --restype javax.jms.QueueConnectionFactory jms/QueueConnectionNotification 
