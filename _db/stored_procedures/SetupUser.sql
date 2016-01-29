CREATE OR REPLACE FUNCTION setup_user(
	user_email TEXT, 
	user_password TEXT
) RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
	query TEXT;
	row RECORD;
BEGIN
	-- Create new user in personal table.
	INSERT INTO User_Pers(email, password) VALUES(user_email, user_password);

	-- Insert the email as the user's first name.
	FOR row IN (SELECT userId 
							FROM User_Pers
							WHERE email=user_email AND password=user_password)
	LOOP
		INSERT INTO User_Gen(userId, nameFirst) VALUES(row.userId, user_email);
		RETURN TRUE;
	END LOOP;

	-- Something went wrong, so clean up.
	DELETE FROM User_Pers
				 WHERE email=user_email AND password=user_password;
	RETURN FALSE;
END;
$$;