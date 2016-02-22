CREATE OR REPLACE FUNCTION groups_info_upsert(
	group_id   INT,
	group_name TEXT,
	group_desc TEXT
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
	WITH upsert AS (UPDATE Groups_info
					SET groupName=group_name,
						groupDescription=group_desc
					WHERE groupId=group_id RETURNING *)
		INSERT INTO Groups_Info(groupId, 
								groupName, 
								groupDescription)
		SELECT group_id, 
			   group_name, 
			   group_desc
		WHERE NOT EXISTS (SELECT *
						  FROM upsert);
END;
$$;