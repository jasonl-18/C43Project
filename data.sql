INSERT INTO users VALUES('jason li', '123 addr', '2003-08-18', 'student', '123456789', 'jason', '1234');
INSERT INTO hosts VALUES('123456789');

INSERT INTO users VALUES('host one', '234 addr', '1997-02-19', 'Real Estate Agent', '111111111', 'one', '1234');
INSERT INTO hosts VALUES('111111111');

INSERT INTO users VALUES('host two', '345 addr', '1997-01-19', 'Investment Banker', '222222222', 'two', '1234');
INSERT INTO hosts VALUES('222222222');

INSERT INTO users VALUES('renter three', '123 random', '1997-11-24', 'TA', '333333333', 'three', '1234');
INSERT INTO renters(sin) VALUES('333333333');

INSERT INTO users VALUES('renter four', '21 live', '1997-12-01', 'TA', '444444444', 'four', '1234');
INSERT INTO renters(sin) VALUES('444444444');

INSERT INTO users VALUES('renter five', '901 suite', '1997-06-15', 'TA', '555555555', 'five', '1234');
INSERT INTO renters(sin) VALUES('555555555');

INSERT INTO listings VALUES( 1, 'House', 43.863471130067595, -79.3106748992169, '124 Main St Unionville','L3R 2H6', 'Markham', 'Canada', 125, '123456789');
INSERT INTO amenities VALUES('Kitchen', 1);
INSERT INTO amenities VALUES('Pool', 1);
INSERT INTO amenities VALUES('Wifi', 1);
INSERT INTO calendar VALUES('2023-08-15', 125, 1);
INSERT INTO calendar VALUES('2023-08-16', 125, 1);
INSERT INTO calendar VALUES('2023-08-17', 125, 1);
INSERT INTO calendar VALUES('2023-08-18', 125, 1);
INSERT INTO calendar VALUES('2023-08-19', 125, 1);

INSERT INTO bookings VALUES('333333333', '2023-08-15', 125, 1);

INSERT INTO listings VALUES( NULL, 'House', 43.8682154167001, -79.30744034377362, '25 Stanford Rd','L3R 6L8', 'Markham', 'Canada', 60, '123456789');
INSERT INTO amenities VALUES('Free Parking', 2);
INSERT INTO amenities VALUES('Jacuzzi', 2);
INSERT INTO amenities VALUES('Washer or Dryer', 2);
INSERT INTO amenities VALUES('Pets allowed', 2);

INSERT INTO listings VALUES( NULL, 'Bed and breakfast', 43.8982154167001, -79.33744034377362, '25 Addr Rd','L3R 7P4', 'Toronto', 'Canada', 60, '123456789');
INSERT INTO amenities VALUES('Free Parking', 2);
INSERT INTO amenities VALUES('Jacuzzi', 2);

INSERT INTO listings VALUES( NULL, 'Secondary unit', 43.8682154167001, -79.30744034377362, '25 Stanford Rd','L3R 6L8', 'Markham', 'Canada', 30, '123456789');

INSERT INTO listings VALUES( NULL, 'Apartment', 33.9635312422403, -118.24544146759526, '1616 E 84th St','90001', 'Los Angeles', 'USA', 100, '111111111');

INSERT INTO cancels VALUES('123456789', 20);
INSERT INTO cancels VALUES('111111111', 15);
INSERT INTO cancels VALUES('333333333', 5);
INSERT INTO cancels VALUES('444444444', 2);



