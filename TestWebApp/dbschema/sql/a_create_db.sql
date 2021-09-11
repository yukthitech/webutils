CREATE SCHEMA `recruitment` DEFAULT CHARACTER SET latin1 COLLATE latin1_bin ;

INSERT INTO users(SPACE_IDENTITY, DISPLAY_NAME, USER_NAME, PASSWORD, CREATED_ON, UPDATED_ON, VERSION, UQ_ENTITY_ID, DELETED) 
VALUES (' ', 'Kranthi', 'kalyani@yukthitech.com', 'nRmUS+uzuXGAUecG+POhoOC3aG0xZvTb1my/u3Kb+a9+5ZwZi+SlAWOuJKwmK67A', now(), now(), 1, 'dummy', false);

insert into user_role(user_id, role, UQ_ENTITY_ID)
values (1, 'ADMIN', 'dummy1'), (1, 'EMPLOYEE', 'dummy2');

CREATE USER 'recruit'@'%' IDENTIFIED WITH mysql_native_password AS 'recruit';
GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON *.* TO 'recruit'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;