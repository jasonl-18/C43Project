create table users (
    name VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    dob DATE NOT NULL,
    occupation VARCHAR(50),
    sin CHAR(9) NOT NULL PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50)
);

create table hosts (
    sin CHAR(9) NOT NULL PRIMARY KEY,
    FOREIGN KEY(sin) REFERENCES users(sin) ON DELETE CASCADE
 );
create table renters (
    sin CHAR(9) NOT NULL PRIMARY KEY,
    card_num CHAR(16),
    FOREIGN KEY(sin) REFERENCES users(sin) ON DELETE CASCADE
);


create table listings (
    lid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(150),
    latitude DECIMAL(7,4) NOT NULL,
    longitude DECIMAL(7,4) NOT NULL, 
    address VARCHAR(100),
    postal VARCHAR(7),
    city VARCHAR(100),
    country VARCHAR(50),
    price DECIMAL(15,2),
    host_sin VARCHAR(50) NOT NULL,
    FOREIGN KEY (host_sin) REFERENCES hosts(sin) ON DELETE CASCADE 
);

create table amenities(
	am_type VARCHAR(30),
	lid INT NOT NULL,
	FOREIGN KEY (lid) REFERENCES listings(lid) ON DELETE CASCADE,
	PRIMARY KEY (am_type, lid)
);


create table calendar (
	date DATE NOT NULL,
	price DECIMAL(15,2),
	lid INT NOT NULL, 
	FOREIGN KEY (lid) REFERENCES listings(lid) ON DELETE CASCADE,
	PRIMARY KEY (lid, date)
);

create table bookings (
	renter_sin CHAR(9) NOT NULL,
	date DATE NOT NULL,
	price DECIMAL(15,2),
	lid INT NOT NULL,
	FOREIGN KEY (lid) REFERENCES listings(lid) ON DELETE CASCADE,
	FOREIGN KEY (renter_sin) REFERENCES renters(sin) ON DELETE CASCADE,
	PRIMARY KEY (renter_sin, lid, date)
);

create table cancels (
	user_sin CHAR(9) NOT NULL,
	count INT,
	FOREIGN KEY (user_sin) REFERENCES users(sin) ON DELETE CASCADE,
	PRIMARY KEY (user_sin)
);

create table user_reviews (
	writer_sin CHAR(9) NOT NULL,
	target_sin CHAR(9) NOT NULL,
	comment VARCHAR(200),
	rating TINYINT,
	FOREIGN KEY (writer_sin) REFERENCES users(sin) ON DELETE CASCADE,
	FOREIGN KEY (target_sin) REFERENCES users(sin) ON DELETE CASCADE,
	PRIMARY KEY (writer_sin, target_sin)
);

create table listing_reviews (
	writer_sin CHAR(9) NOT NULL,
	target_lid INT NOT NULL,
	comment VARCHAR(200),
	rating TINYINT,
	FOREIGN KEY (writer_sin) REFERENCES renters(sin) ON DELETE CASCADE,
	FOREIGN KEY (target_lid) REFERENCES listings(lid) ON DELETE CASCADE,
	PRIMARY KEY (writer_sin, target_lid)
);

