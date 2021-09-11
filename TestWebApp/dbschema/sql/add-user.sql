set @name := 'Sudha';
set @mailId := 'sudha@yukthitech.com';
set @user := 'sudha';
set @password := 'M4njxNiYpdjRjXD2axBx4IMsPoV5sAKoShS023eAWvEcNxg13m58HJExxiTYodco'; /* $udha@123 */
set @phoneNo := '8743995839';

set @nextId = (SELECT MAX(ID) + 1 FROM WEBUTILS_USERS);

INSERT INTO `WEBUTILS_USERS` (`ID`,`SPACE_IDENTITY`,`UPDATED_BY_ID`,`DISPLAY_NAME`,`UPDATED_ON`,
`USER_NAME`,`VERSION`,`CREATED_ON`,`BASE_ENT_TYPE`,`PASSWORD`,`OWNER_ENT_TYPE`,`DELETED`,
`CREATED_BY_ID`,`BASE_ENT_ID`,`OWNER_ENT_ID`,`UQ_ENTITY_ID`) 
VALUES 
(@nextId,'',NULL, @name, now(), @mailId,1,now(),NULL,
@password,NULL,0,NULL,NULL,NULL,concat('dummy', @nextId));


set @roleId = (SELECT MAX(ID) + 1 FROM RECRUIT_USER_ROLE);
INSERT INTO `RECRUIT_USER_ROLE` (`ID`, `ROLE`, `USER_ID`, `UQ_ENTITY_ID`) VALUES (@roleId, 'EMPLOYEE', @nextId, concat('DUMMY-', @roleId));


set @empId = (SELECT MAX(ID) + 1 FROM RECRUIT_EMP);
INSERT INTO `RECRUIT_EMP` (`ID`, `VERSION`, `SPACE_IDENTITY`, `USER_ID`, `JOIN_DATE`, `CREATED_ON`, PHONE_NO) 
VALUES (@empId, '0', '', @nextId, now(), now(), @phoneNo);

