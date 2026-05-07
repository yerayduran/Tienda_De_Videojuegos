USE mi_coleccion_db;

INSERT INTO GENERO (nombre) VALUES
('RPG'),
('Accion'),
('Aventura'),
('Estrategia'),
('Deportes');

INSERT INTO PLATAFORMA (nombre) VALUES
('PC'),
('PlayStation 5'),
('Nintendo Switch'),
('Xbox Series X'),
('Steam Deck');

INSERT INTO VIDEOJUEGO (titulo, año, nota, id_genero) VALUES
('The Witcher 3', 2015, 10, 1),
('Hades', 2020, 9, 2),
('Zelda: Tears of the Kingdom', 2023, 10, 3);

INSERT INTO VIDEOJUEGO_PLATAFORMA (id_videojuego, id_plataforma) VALUES
(1, 1), (1, 2), (1, 5),
(2, 1), (2, 3), (2, 5),
(3, 3);

INSERT INTO COMPRA (id_videojuego, fecha, precio, tienda) VALUES
(1, '2024-01-14', 29.99, 'Steam'),
(2, '2024-02-01', 19.99, 'Nintendo eShop'),
(3, '2025-05-03', 69.99, 'GAME');