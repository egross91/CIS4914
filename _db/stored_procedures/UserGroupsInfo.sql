CREATE OR REPLACE FUNCTION user_groups_info(
	user_id INT
) RETURNS TABLE(groupId          INT,
				groupName        TEXT,
				groupDescription TEXT
				) LANGUAGE plpgsql AS $$
BEGIN
	RETURN QUERY (SELECT * 
				  FROM (SELECT gi.groupId,
				  			   gi.groupName,
				  			   gi.groupDescription
				  		FROM Groups_Info gi
				  		WHERE gi.groupId IN (SELECT ug.groupId
				  							 FROM User_Groups ug
				  							 WHERE user_id=ANY(ug.groupMembers::INT[])
				  							)
				  		) outerquery
				  );
END;
$$;