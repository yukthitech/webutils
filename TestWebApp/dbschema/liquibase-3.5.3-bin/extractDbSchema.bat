SET DB_URL=jdbc:mysql://localhost:3306/recruitment
SET DB_USER=kranthi
SET DB_PASSWORD=kranthi

cd /D %~dp0

liquibase --driver=com.mysql.jdbc.Driver --classpath=.\mysql-connector-java-5.1.9.jar --changeLogFile=base.xml --url="%DB_URL%" --username=%DB_USER% --password=%DB_PASSWORD% generateChangeLog

pause
pause
