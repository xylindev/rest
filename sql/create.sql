DROP TABLE IF EXISTS accepts;
DROP TABLE IF EXISTS Deposit;
DROP TABLE IF EXISTS WasteType;
DROP TABLE IF EXISTS CollectionPoint;
DROP TABLE IF EXISTS users;

CREATE TABLE WasteType (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    pointsPerKilo INT NOT NULL
);

CREATE TABLE CollectionPoint (
    id INT PRIMARY KEY,
    adresse VARCHAR(255) NOT NULL,
    capaciteMax FLOAT NOT NULL
);

CREATE TABLE accepts (
    pointId INT,
    wasteTypeId INT,
    PRIMARY KEY (pointId, wasteTypeId),
    FOREIGN KEY (pointId) REFERENCES CollectionPoint(id),
    FOREIGN KEY (wasteTypeId) REFERENCES WasteType(id)
);

CREATE TABLE users (
    id INT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('USER', 'ADMIN')) NOT NULL
);

CREATE TABLE Deposit (
    id SERIAL PRIMARY KEY,
    userId INT,
    pointId INT,
    wasteTypeId INT,
    poids FLOAT NOT NULL,
    dateDepot TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    collecte BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (pointId) REFERENCES CollectionPoint(id),
    FOREIGN KEY (wasteTypeId) REFERENCES WasteType(id)
);
