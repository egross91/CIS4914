CREATE OR REPLACE FUNCTION setup_user(
	user_email TEXT, 
	user_password TEXT
) RETURNS INT LANGUAGE plpgsql AS $$
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
		-- Create row in General information.
		INSERT INTO User_Gen(userId, nameFirst) VALUES(row.userId, user_email);

		-- Create row in Device information.
		INSERT INTO User_Device(userId) VALUES(row.userId);

		-- Success.
		RETURN row.userId;
	END LOOP;

	-- Something went wrong, so clean up.
	DELETE FROM User_Pers
		   WHERE email=user_email AND password=user_password;
	RETURN -1;
END;
$$;