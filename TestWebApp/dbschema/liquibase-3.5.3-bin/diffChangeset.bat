cd D:\p10\razor\devops\sdp\Tools\DBMigration\liquibase-3.5.3-bin

liquibase --driver=com.mysql.jdbc.Driver --classpath=C:\Users\ramum\.m2\repository\mysql\mysql-connector-java\5.1.9\mysql-connector-java-5.1.9.jar --changeLogFile=D:\p10\razor\devops\sdp\Tools\DBMigration\ver_3_0_1.xml --url="jdbc:mysql://localhost:3306/sdp_qa_db" --username=root --password=1234 diffChangeLog --referenceUrl="jdbc:mysql://localhost:3306/production4"  --referenceUsername=root --referencePassword=1234


pause
