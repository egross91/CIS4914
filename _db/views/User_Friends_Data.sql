CREATE VIEW user_friends_data AS
	SELECT ug.nameFirst, ug.nameLast, uf.userId
	FROM User_Gen ug, User_Friends uf
	WHERE ug.userId=uf.friendId
;