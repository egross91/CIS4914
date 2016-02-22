CREATE TABLE User_Groups (
	groupId      INT,
	groupMembers INT ARRAY NOT NULL, -- userId's from User_Pers.
	PRIMARY KEY (groupId)
);