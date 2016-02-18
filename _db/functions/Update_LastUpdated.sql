CREATE OR REPLACE FUNCTION update_lastupdated() 
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
	NEW.lastUpdated = now();
	RETURN NEW;
END;
$$;