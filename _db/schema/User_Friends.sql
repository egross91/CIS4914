CREATE TABLE User_Friends (
	userId    INT NOT NULL,
	friendIds INT ARRAY,
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE,
	CHECK (userId<>ANY(friendIds::INT[])) -- Cannot be friends with yourself.
);