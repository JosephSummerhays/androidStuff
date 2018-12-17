DROP TABLE IF EXISTS nameAndPassword;
DROP TABLE IF EXISTS userInfo;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS treeRoot;
DROP TABLE IF EXISTS parents;
DROP TABLE IF EXISTS family;
DROP TABLE IF EXISTS eventPeople;
DROP TABLE IF EXISTS eventPosition;
DROP TABLE IF EXISTS eventLocation;
DROP TABLE IF EXISTS eventInfo;

CREATE TABLE nameAndPassword
(
	auth_token char(64) NOT NULL UNIQUE,
	username char(64) NOT NULL UNIQUE,
	passwrd char(64) NOT NULL,
	CONSTRAINT nameAndPassword_user_pk PRIMARY KEY (auth_token, username)
);

CREATE TABLE userInfo
(
	auth_token char(32) NOT NULL UNIQUE,
	email char(128) UNIQUE,
	first_name char(32) NOT NULL,
	last_name char(32) NOT NULL,
	gender char(1) NOT NULL,
	CONSTRAINT userInfo_user_pk PRIMARY KEY (auth_token),
	CONSTRAINT userInfo_user_fk
		FOREIGN KEY (auth_token)
		REFERENCES nameAndPassword(auth_token)
);

CREATE TABLE people
(
	person_ID char(64) NOT NULL UNIQUE,
	first_name char(32) NOT NULL,
	last_name char(32),
	gender char(1) NOT NULL,
	CONSTRAINT people_person_ID_pk PRIMARY KEY (person_ID)
);

CREATE TABLE treeRoot
(
	auth_token char(64) NOT NULL UNIQUE,
	person_ID char(64) NOT NULL UNIQUE,
	CONSTRAINT treeRoot_user_pk PRIMARY KEY (auth_token),
	CONSTRAINT treeRoot_user_fk
		FOREIGN KEY (auth_token)
		REFERENCES nameAndPassword(auth_token),
	CONSTRAINT treeRoot_people_FK
		FOREIGN KEY (person_ID)
		REFERENCES people(person_ID)
);

CREATE TABLE parents
(
	person_ID char(64) NOT NULL UNIQUE,
	father_ID char(64),
	mother_ID char(64),
	CONSTRAINT parents_person_ID_PK PRIMARY KEY (person_ID),
	CONSTRAINT parents_person_ID_FK
		FOREIGN KEY (person_ID)
		REFERENCES people(person_ID),
	CONSTRAINT parents_father_ID_FK
		FOREIGN KEY (father_ID)
		REFERENCES people(person_ID),
	CONSTRAINT parents_mother_ID_FK
		FOREIGN KEY (mother_ID)
		REFERENCES people(person_ID)
);

CREATE TABLE family
(
	person_ID char(64) NOT NULL,
	spouse_ID char(64),
	owner_ID char(64) NOT NULL,
	CONSTRAINT family_person_ID_PK PRIMARY KEY (person_ID),
	CONSTRAINT family_person_FK
		FOREIGN KEY (person_ID)
		REFERENCES people(person_ID),
	CONSTRAINT family_spouse_FK
		FOREIGN KEY (spouse_ID)
		REFERENCES people(person_ID),
	CONSTRAINT family_owner_ID_FK
		FOREIGN KEY (owner_ID)
		REFERENCES treeRoot(person_ID)
);

CREATE TABLE eventPeople
(
	event_ID char(64) NOT NULL UNIQUE,
	auth_token char(64) NOT NULL,
	person_ID char(64) NOT NULL,
	CONSTRAINT eventPeople_pk PRIMARY KEY (event_ID),
	CONSTRAINT eventPeople_user_fk
		FOREIGN KEY (auth_token)
		REFERENCES nameAndPassword(auth_token),
	CONSTRAINT eventPeople_person_fk
		FOREIGN KEY (person_ID)
		REFERENCES people(person_ID)
);

CREATE TABLE eventPosition
(
	event_ID char(64) NOT NULL UNIQUE,
	latitude float NOT NULL,
	longitude float NOT NULL,
	CONSTRAINT eventPosition_ID_pk PRIMARY KEY (event_ID),
	CONSTRAINT eventPosition_fk
		FOREIGN KEY (event_ID)
		REFERENCES eventPeople(event_ID)
);
CREATE TABLE eventLocation
(
	event_ID char(64) NOT NULL,
	country char(32) NOT NULL,
	city char(32),
	CONSTRAINT event_ID_pk PRIMARY KEY (event_ID),
	CONSTRAINT event_ID_fk
		FOREIGN KEY (event_ID)
		REFERENCES eventPeople(event_ID)
);
CREATE TABLE eventInfo
(
	event_ID char(64) NOT NULL,
	event_type char(32) NOT NULL,
	event_year int NOT NULL,
	CONSTRAINT event_pk PRIMARY KEY (event_ID),
	CONSTRAINT event_fk
		FOREIGN KEY (event_ID)
		REFERENCES eventPeople(event_ID)
);