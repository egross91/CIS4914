CREATE TABLE User_Groups (
	groupId      INT,
	groupMembers INT ARRAY NOT NULL, -- userId's from User_Pers.
	FOREIGN KEY (groupId)
		REFERENCES Groups_Info(groupId) ON DELETE CASCADE
										ON UPDATE CASCADE
);