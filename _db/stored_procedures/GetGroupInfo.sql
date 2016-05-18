CREATE OR REPLACE FUNCTION get_group_info(
	group_id BIGINT
) RETURNS TABLE(userId    INT,
				nameFirst CHARACTER VARYING,
				nameLast  CHARACTER VARYING
				) LANGUAGE plpgsql AS $$
BEGIN
	RETURN QUERY (SELECT *
				  FROM (SELECT ug.userId,
				  			   ug.nameFirst,
				  			   ug.nameLast
				  		FROM User_Gen ug
				  		WHERE ug.userId=ANY((SELECT ugr.groupMembers
				  						 	FROM User_Groups ugr
				  						 	WHERE ugr.groupId=group_id
				  						 	)::INT[])
				  		) outerquery
				  );
END;
$$;