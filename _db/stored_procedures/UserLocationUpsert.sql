CREATE OR REPLACE FUNCTION user_location_upsert(
	user_id        INT,
	user_longitude NUMERIC(7, 4),
	user_latitude  NUMERIC(7, 4)
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
	WITH upsert AS (UPDATE User_Loc
					SET longitude=user_longitude,
						latitude=user_latitude
					WHERE userId=user_id RETURNING *)
		INSERT INTO User_Loc(userId,
							 longitude,
							 latitude)
		SELECT user_id,
			   user_longitude,
			   user_latitude
		WHERE NOT EXISTS (SELECT *
						  FROM upsert);
END;
$$;