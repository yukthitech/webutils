SET DB_URL=jdbc:mysql://148.72.211.139:3306/recruitment
SET DB_USER=recruit
SET DB_PASSWORD=recruit@Yuk1

cd /D %~dp0

call .\liquibase-3.5.3-bin\liquibase --driver=com.mysql.jdbc.Driver --classpath=.\liquibase-3.5.3-bin\mysql-connector-java-8.0.20.jar --logLevel=info --changeLogFile=master.xml --url="%DB_URL%" --username=%DB_USER% --password=%DB_PASSWORD% update

pause
