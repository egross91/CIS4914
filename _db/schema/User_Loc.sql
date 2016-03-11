CREATE TABLE User_Loc (
	userId    INT NOT NULL,
	longitude NUMERIC(7, 4), -- ABC.WXYZ
	latitude  NUMERIC(7, 4), -- ABC.WXYZ
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE
									 ON UPDATE CASCADE
);