SET DB_URL=jdbc:mysql://localhost:3306/test
SET DB_USER=kranthi
SET DB_PASSWORD=kranthi

cd /D %~dp0

call .\liquibase-3.5.3-bin\liquibase --driver=com.mysql.jdbc.Driver --classpath=.\liquibase-3.5.3-bin\mysql-connector-java-8.0.20.jar --logLevel=info --changeLogFile=master.xml --url="%DB_URL%" --username=%DB_USER% --password=%DB_PASSWORD% update

pause
