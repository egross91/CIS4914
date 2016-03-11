CREATE TABLE User_Device (
	userId           INT NOT NULL,
	deviceId         VARCHAR(64),
	deviceIp         VARCHAR(40),
	deviceSerial     VARCHAR(100),
	deviceDevice     VARCHAR(100),
	deviceModel      VARCHAR(100),
	deviceProduct    VARCHAR(100),
	deviceBrand      VARCHAR(100),
	deviceSdkVersion INT,
	lastUpdated      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (userId)
		REFERENCES User_Pers(userId) ON DELETE CASCADE
									 ON UPDATE CASCADE
);