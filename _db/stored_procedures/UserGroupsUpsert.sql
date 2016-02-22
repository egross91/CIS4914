CREATE OR REPLACE FUNCTION user_groups_upsert(
	group_id      INT,
	group_members INT ARRAY
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
	WITH upsert AS (UPDATE User_Groups
					SET groupMembers=group_members
					WHERE groupId=group_id RETURNING *)
		INSERT INTO User_Groups(groupId, 
								groupMembers)
		SELECT group_id, 
			   group_members
		WHERE NOT EXISTS (SELECT *
						  FROM upsert);
END;
$$;