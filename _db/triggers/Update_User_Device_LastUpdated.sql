CREATE TRIGGER update_user_device_lastupdated BEFORE UPDATE
ON User_Device 
FOR EACH ROW 
EXECUTE PROCEDURE update_lastupdated();