INSERT INTO WasteType VALUES
(1, 'Batteries', 15),
(2, 'Textiles', 5),
(3, 'Electroniques', 20),
(4, 'Verre', 2),
(5, 'Plastique', 3);

INSERT INTO CollectionPoint VALUES
(1, '12 Rue de l''Innovation, Lille', 500),
(2, 'Place de la Gare, Villeneuve d''Ascq', 300),
(3, 'Dépôt V2, Villeneuve d''Ascq', 200);

INSERT INTO accepts (pointid, wastetypeid) VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 4),
(2, 5),
(3, 2),
(3, 3),
(3, 4),
(3, 5);

INSERT INTO users VALUES
(1, 'pierre', 'pierre', 'ADMIN'),
(2, 'paul',   'paul',   'USER'),
(3, 'marie',  'marie',  'USER'),
(4, 'lucas',  'lucas',  'USER'),
(5, 'admin',  'admin',  'ADMIN');

-- id est SERIAL, on ne le fournit pas
INSERT INTO Deposit (userId, pointId, wasteTypeId, poids, collecte) VALUES
(1, 1, 1, 5.5, FALSE),
(2, 1, 2, 10.0, FALSE),
(1, 2, 3, 2.5, TRUE),
(2, 3, 1, 7.0, FALSE),
(1, 3, 3, 1.2, FALSE);
