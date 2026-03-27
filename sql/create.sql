DROP TABLE IF EXISTS accepts;
DROP TABLE IF EXISTS WasteType;
DROP TABLE IF EXISTS CollectionPoint;
DROP TABLE IF EXISTS users;

CREATE TABLE WasteType(
    id INT PRIMARY KEY,
    name TEXT NOT NULL,
    pointsPerKilo INT NOT NULL
);

CREATE TABLE CollectionPoint(
    id INT PRIMARY KEY,
    adresse TEXT NOT NULL,
    capaciteMax INT NOT NULL
);

CREATE TABLE accepts(
    pointid INT REFERENCES CollectionPoint(id) ON DELETE CASCADE,
    wastetypeid INT REFERENCES WasteType(id) ON DELETE CASCADE,
    PRIMARY KEY(pointid, wastetypeid)
);

CREATE TABLE users(
    id INT PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);