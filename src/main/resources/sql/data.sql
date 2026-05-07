USE mi_coleccion_db;

-- 1. Inserción de Géneros
INSERT INTO GENERO (id_genero, nombre) VALUES
                                           (1, 'RPG'), (2, 'Accion'), (3, 'Aventura'), (4, 'Estrategia'), (5, 'Deportes');

-- 2. Inserción de Plataformas
INSERT INTO PLATAFORMA (id_plataforma, nombre) VALUES
                                                   (1, 'PC'), (2, 'PlayStation 5'), (3, 'Nintendo Switch'), (4, 'Xbox Series X'), (5, 'Steam Deck');

-- 3. Inserción de Videojuegos (Se eliminó la columna id_genero de aquí)
INSERT INTO VIDEOJUEGO (id_videojuego, titulo, año, nota, url_portada) VALUES
                                                                           (1, 'Cyberpunk 2077', 2020, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1091500/library_600x900.jpg'),
                                                                           (2, 'Persona 5 Royal', 2019, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1687950/library_600x900.jpg'),
                                                                           (3, 'Final Fantasy VII Remake', 2020, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1462040/library_600x900.jpg'),
                                                                           (4, 'Dragon Quest XI', 2017, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1295510/library_600x900.jpg'),
                                                                           (5, 'The Elder Scrolls V: Skyrim', 2011, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/489830/library_600x900.jpg'),
                                                                           (6, 'Fallout 4', 2015, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/377160/library_600x900.jpg'),
                                                                           (7, 'Mass Effect Legendary Edition', 2021, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1328670/library_600x900.jpg'),
                                                                           (8, 'Bloodborne', 2015, 10, NULL),
                                                                           (9, 'Dark Souls III', 2016, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/374320/library_600x900.jpg'),
                                                                           (10, 'Monster Hunter World', 2018, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/582010/library_600x900.jpg'),
                                                                           (11, 'Red Dead Redemption 2', 2018, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1174180/library_600x900.jpg'),
                                                                           (12, 'The Last of Us Part II', 2020, 10, NULL),
                                                                           (13, 'Uncharted 4: El Desenlace del Ladrón', 2016, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1659420/library_600x900.jpg'),
                                                                           (14, 'Horizon Zero Dawn', 2017, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1151640/library_600x900.jpg'),
                                                                           (15, 'Ghost of Tsushima', 2020, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2215430/library_600x900.jpg'),
                                                                           (16, 'Super Mario Odyssey', 2017, 10, NULL),
                                                                           (17, 'Marvel''s Spider-Man Remastered', 2018, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1817070/library_600x900.jpg'),
                                                                           (18, 'Doom Eternal', 2020, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/782330/library_600x900.jpg'),
                                                                           (19, 'Devil May Cry 5', 2019, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/601150/library_600x900.jpg'),
                                                                           (20, 'Bayonetta 3', 2022, 8, NULL),
                                                                           (21, 'Sekiro: Shadows Die Twice', 2019, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/814380/library_600x900.jpg'),
                                                                           (22, 'Resident Evil 4 Remake', 2023, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2050650/library_600x900.jpg'),
                                                                           (23, 'Dead Space Remake', 2023, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1693980/library_600x900.jpg'),
                                                                           (24, 'Halo Infinite', 2021, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1240440/library_600x900.jpg'),
                                                                           (25, 'Gears 5', 2019, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1097840/library_600x900.jpg'),
                                                                           (26, 'Returnal', 2021, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1649240/library_600x900.jpg'),
                                                                           (27, 'Civilization VI', 2016, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/289070/library_600x900.jpg'),
                                                                           (28, 'XCOM 2', 2016, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/268500/library_600x900.jpg'),
                                                                           (29, 'Crusader Kings III', 2020, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1158310/library_600x900.jpg'),
                                                                           (30, 'Starcraft II', 2010, 10, NULL),
                                                                           (31, 'Total War: Warhammer III', 2022, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1145980/library_600x900.jpg'),
                                                                           (32, 'Stellaris', 2016, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/281990/library_600x900.jpg'),
                                                                           (33, 'Hearts of Iron IV', 2016, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/394360/library_600x900.jpg'),
                                                                           (34, 'Company of Heroes 3', 2023, 7, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1677280/library_600x900.jpg'),
                                                                           (35, 'Age of Mythology: Retold', 2024, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1934680/library_600x900.jpg'),
                                                                           (36, 'Warcraft III: Reforged', 2020, 5, NULL),
                                                                           (37, 'NBA 2K24', 2023, 7, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2338770/library_600x900.jpg'),
                                                                           (38, 'Madden NFL 24', 2023, 6, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2140330/library_600x900.jpg'),
                                                                           (39, 'F1 23', 2023, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2108330/library_600x900.jpg'),
                                                                           (40, 'Gran Turismo 7', 2022, 9, NULL),
                                                                           (41, 'Forza Horizon 5', 2021, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1551360/library_600x900.jpg'),
                                                                           (42, 'EA SPORTS FC 24', 2023, 8, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2195250/library_600x900.jpg'),
                                                                           (43, 'Rocket League', 2015, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/252950/library_600x900.jpg'),
                                                                           (44, 'Tony Hawk''s Pro Skater 1+2', 2020, 9, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2395210/library_600x900.jpg'),
                                                                           (45, 'Mario Kart 8 Deluxe', 2017, 10, NULL),
                                                                           (46, 'eFootball 2024', 2023, 6, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1665460/library_600x900.jpg'),
                                                                           (47, 'Baldur''s Gate 3', 2023, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1086940/library_600x900.jpg'),
                                                                           (48, 'Alan Wake 2', 2023, 9, NULL),
                                                                           (49, 'Super Smash Bros. Ultimate', 2018, 10, NULL),
                                                                           (50, 'Hollow Knight', 2017, 10, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/367520/library_600x900.jpg');

-- 4. Inserción en Tabla Puente Géneros (Relacionando juegos con sus géneros)
INSERT INTO VIDEOJUEGO_GENERO (id_videojuego, id_genero) VALUES
                                                             (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1), (47, 1), -- RPG
                                                             (17, 2), (18, 2), (19, 2), (20, 2), (21, 2), (22, 2), (23, 2), (24, 2), (25, 2), (26, 2), (49, 2), -- Acción
                                                             (11, 3), (12, 3), (13, 3), (14, 3), (15, 3), (16, 3), (48, 3), (50, 3), -- Aventura
                                                             (27, 4), (28, 4), (29, 4), (30, 4), (31, 4), (32, 4), (33, 4), (34, 4), (35, 4), (36, 4), -- Estrategia
                                                             (37, 5), (38, 5), (39, 5), (40, 5), (41, 5), (42, 5), (43, 5), (44, 5), (45, 5), (46, 5); -- Deportes

-- 5. Inserción en Tabla Puente Plataformas
INSERT INTO VIDEOJUEGO_PLATAFORMA (id_videojuego, id_plataforma) VALUES
                                                                     (1, 1), (1, 2), (1, 4), (1, 5), (2, 1), (2, 2), (2, 3), (2, 4), (3, 1), (3, 2), (3, 5), (4, 1), (4, 2), (4, 3),
                                                                     (5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (6, 1), (6, 2), (6, 4), (7, 1), (7, 2), (7, 4), (8, 2),
                                                                     (9, 1), (9, 2), (9, 4), (9, 5), (10, 1), (10, 2), (10, 4), (11, 1), (11, 2), (11, 4), (12, 2), (13, 1), (13, 2),
                                                                     (14, 1), (14, 2), (15, 1), (15, 2), (16, 3), (17, 1), (17, 2), (18, 1), (18, 2), (18, 3), (18, 4), (19, 1), (19, 2), (19, 4),
                                                                     (20, 3), (21, 1), (21, 2), (21, 4), (22, 1), (22, 2), (22, 4), (23, 1), (23, 2), (23, 4), (24, 1), (24, 4), (25, 1), (25, 4),
                                                                     (26, 1), (26, 2), (27, 1), (27, 3), (27, 4), (28, 1), (28, 2), (28, 3), (28, 4), (29, 1), (29, 2), (29, 4), (30, 1),
                                                                     (31, 1), (32, 1), (32, 2), (32, 4), (33, 1), (34, 1), (35, 1), (35, 4), (36, 1), (37, 1), (37, 2), (37, 4),
                                                                     (38, 1), (38, 2), (38, 4), (39, 1), (39, 2), (39, 4), (40, 2), (41, 1), (41, 4), (42, 1), (42, 2), (42, 3), (42, 4),
                                                                     (43, 1), (43, 2), (43, 3), (43, 4), (44, 1), (44, 2), (44, 3), (44, 4), (45, 3), (46, 1), (46, 2), (46, 4),
                                                                     (47, 1), (47, 2), (47, 4), (47, 5), (48, 1), (48, 2), (48, 4), (49, 3), (50, 1), (50, 2), (50, 3), (50, 4), (50, 5);

-- 6. Inserción de Compras
INSERT INTO COMPRA (id_compra, id_videojuego, fecha, precio, tienda) VALUES
                                                                         (1, 1, '2020-12-10', 59.99, 'GOG'), (2, 2, '2022-10-21', 49.99, 'Steam'), (3, 3, '2021-06-15', 69.99, 'PlayStation Store'),
                                                                         (4, 4, '2019-11-20', 39.99, 'Nintendo eShop'), (5, 5, '2015-05-14', 19.99, 'Steam'), (6, 6, '2016-01-10', 29.99, 'GAME'),
                                                                         (7, 7, '2021-07-05', 59.99, 'Origin'), (8, 8, '2017-03-22', 19.99, 'PlayStation Store'), (9, 9, '2018-08-11', 14.99, 'Steam'),
                                                                         (10, 10, '2019-02-14', 24.99, 'Amazon'), (11, 11, '2018-10-26', 69.99, 'GAME'), (12, 12, '2020-06-19', 59.99, 'PlayStation Store'),
                                                                         (13, 13, '2017-05-10', 19.99, 'Fnac'), (14, 14, '2018-04-12', 19.99, 'PlayStation Store'), (15, 15, '2021-02-15', 39.99, 'Amazon'),
                                                                         (16, 16, '2017-12-25', 49.99, 'Nintendo eShop'), (17, 17, '2019-09-07', 29.99, 'GAME'), (18, 18, '2020-05-20', 39.99, 'Steam'),
                                                                         (19, 19, '2019-11-11', 29.99, 'Xbox Store'), (20, 20, '2022-10-28', 59.99, 'Nintendo eShop'), (21, 21, '2020-01-15', 39.99, 'Steam'),
                                                                         (22, 22, '2023-04-05', 69.99, 'PlayStation Store'), (23, 23, '2023-02-10', 59.99, 'EA App'), (24, 24, '2021-12-15', 59.99, 'Xbox Store'),
                                                                         (25, 25, '2020-03-12', 19.99, 'Steam'), (26, 26, '2021-08-22', 49.99, 'PlayStation Store'), (27, 27, '2018-06-14', 29.99, 'Steam'),
                                                                         (28, 28, '2017-09-09', 19.99, 'Steam'), (29, 29, '2021-01-10', 39.99, 'Steam'), (30, 30, '2012-07-27', 39.99, 'Battle.net'),
                                                                         (31, 31, '2022-04-18', 49.99, 'Steam'), (32, 32, '2018-05-09', 14.99, 'Steam'), (33, 33, '2019-06-06', 19.99, 'Steam'),
                                                                         (34, 34, '2023-03-15', 59.99, 'Steam'), (35, 35, '2024-09-04', 29.99, 'Xbox Store'), (36, 36, '2020-02-14', 29.99, 'Battle.net'),
                                                                         (37, 37, '2023-11-20', 39.99, 'PlayStation Store'), (38, 38, '2023-12-25', 34.99, 'Xbox Store'), (39, 39, '2023-07-16', 59.99, 'Steam'),
                                                                         (40, 40, '2022-05-10', 69.99, 'PlayStation Store'), (41, 41, '2021-11-15', 59.99, 'Xbox Store'), (42, 42, '2022-10-01', 49.99, 'EA App'),
                                                                         (43, 43, '2016-08-12', 19.99, 'Steam'), (44, 44, '2021-04-10', 29.99, 'Epic Games Store'), (45, 45, '2018-01-15', 49.99, 'Nintendo eShop'),
                                                                         (46, 46, '2023-09-07', 0.00, 'Steam'), (47, 47, '2023-08-10', 59.99, 'GOG'), (48, 48, '2023-10-31', 49.99, 'Epic Games Store'),
                                                                         (49, 49, '2019-02-28', 59.99, 'GAME'), (50, 50, '2018-07-20', 14.99, 'Steam');