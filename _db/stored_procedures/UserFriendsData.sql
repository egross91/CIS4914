CREATE OR REPLACE FUNCTION user_friends_data(
	user_id INT
) RETURNS TABLE(nameFirst CHARACTER VARYING,
				nameLast  CHARACTER VARYING,
				friendId  INT
				) LANGUAGE plpgsql AS $$
BEGIN
	RETURN QUERY (SELECT *
				  FROM (SELECT ug.nameFirst,
				  			   ug.nameLast,
				  			   ug.userId
				  		FROM User_Gen ug
				  		WHERE ug.userId IN (SELECT uf.friendId
				  							FROM User_Friends uf
				  						 	WHERE uf.userId=user_id
				  						 	)
				  		) outerquery
				  );
END;
$$;