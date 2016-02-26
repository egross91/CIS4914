CREATE OR REPLACE FUNCTION user_friends_upsert(
	user_id    INT,
	friend_ids INT ARRAY
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
	WITH upsert AS (UPDATE User_Friends
					SET friendIds=friend_ids
					WHERE userId=user_id RETURNING *)
		INSERT INTO User_Friends(userId, 
								 friendIds)
		SELECT user_id,
			   friend_ids
		WHERE NOT EXISTS (SELECT *
						  FROM upsert);
END;
$$;