-- 1. CREACIÓN DE LA ESTRUCTURA (DDL)
DROP DATABASE IF EXISTS mi_coleccion_db;
CREATE DATABASE mi_coleccion_db;
USE mi_coleccion_db;

-- Tabla de Géneros
CREATE TABLE GENERO (
                        id_genero INT PRIMARY KEY AUTO_INCREMENT,
                        nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla de Plataformas
CREATE TABLE PLATAFORMA (
                            id_plataforma INT PRIMARY KEY AUTO_INCREMENT,
                            nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla de Videojuegos (Normalizada: sin id_genero directo)
CREATE TABLE VIDEOJUEGO (
                            id_videojuego INT PRIMARY KEY AUTO_INCREMENT,
                            titulo VARCHAR(150) NOT NULL,
                            año INT,
                            nota INT CHECK (nota >= 1 AND nota <= 10),
                            url_portada VARCHAR(255)
);

-- Tabla Puente: VIDEOJUEGO_GENERO (Relación N:M)
-- Esto permite que un juego tenga varios géneros (ej. RPG y Acción)
CREATE TABLE VIDEOJUEGO_GENERO (
                                   id_videojuego INT NOT NULL,
                                   id_genero INT NOT NULL,
                                   PRIMARY KEY (id_videojuego, id_genero),
                                   CONSTRAINT fk_vg_videojuego FOREIGN KEY (id_videojuego)
                                       REFERENCES VIDEOJUEGO(id_videojuego) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT fk_vg_genero FOREIGN KEY (id_genero)
                                       REFERENCES GENERO(id_genero) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla Puente: VIDEOJUEGO_PLATAFORMA (Relación N:M)
CREATE TABLE VIDEOJUEGO_PLATAFORMA (
                                       id_videojuego INT NOT NULL,
                                       id_plataforma INT NOT NULL,
                                       PRIMARY KEY (id_videojuego, id_plataforma),
                                       CONSTRAINT fk_vs_videojuego FOREIGN KEY (id_videojuego)
                                           REFERENCES VIDEOJUEGO(id_videojuego) ON DELETE CASCADE ON UPDATE CASCADE,
                                       CONSTRAINT fk_vs_plataforma FOREIGN KEY (id_plataforma)
                                           REFERENCES PLATAFORMA(id_plataforma) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla de Compras
CREATE TABLE COMPRA (
                        id_compra INT PRIMARY KEY AUTO_INCREMENT,
                        id_videojuego INT NOT NULL,
                        fecha DATE NOT NULL,
                        precio DECIMAL(10, 2),
                        tienda VARCHAR(100),
                        CONSTRAINT fk_compra_videojuego FOREIGN KEY (id_videojuego)
                            REFERENCES VIDEOJUEGO(id_videojuego) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_videojuego_titulo ON VIDEOJUEGO(titulo);
CREATE INDEX idx_compra_videojuego ON COMPRA(id_videojuego);