CREATE TABLE Groups_Info (
	groupId          BIGSERIAL NOT NULL,
	groupName        TEXT NOT NULL,
	groupDescription TEXT,
	PRIMARY KEY (groupId)
);