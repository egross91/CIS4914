CREATE TABLE User_Gen (
	userId    INT NOT NULL,
	nameFirst VARCHAR(40),
	nameLast  VARCHAR(70),
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE
);