CREATE TABLE User_Pers (
	userId    SERIAL NOT NULL,
	email     VARCHAR(255) NOT NULL,
	password  VARCHAR(75) NOT NULL,
	PRIMARY KEY (userId)
);