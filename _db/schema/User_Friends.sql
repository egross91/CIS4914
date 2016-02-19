CREATE TABLE User_Friends (
	userId   INT NOT NULL,
	friendId INT NOT NULL,
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE,
	FOREIGN KEY (friendId)
		REFERENCES User_Pers(userId),
	CHECK (userId <> friendId) -- Cannot be friends with yourself.
);