CREATE TABLE User_Groups (
	userId  INT NOT NULL,
	groupId SERIAL,
	PRIMARY KEY (groupId),
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE
);