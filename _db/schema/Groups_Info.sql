CREATE TABLE Groups_Info (
	groupId          SERIAL NOT NULL,
	groupName        TEXT NOT NULL,
	groupDescription TEXT,
	FOREIGN KEY (groupId)
		REFERENCES User_Groups(groupId) ON DELETE CASCADE
);