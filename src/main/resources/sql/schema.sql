DROP DATABASE IF EXISTS mi_coleccion_db;
CREATE DATABASE mi_coleccion_db;
USE mi_coleccion_db;

CREATE TABLE GENERO (
    id_genero INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE PLATAFORMA (
    id_plataforma INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE VIDEOJUEGO (
    id_videojuego INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    año INT,
    nota INT CHECK (nota >= 1 AND nota <= 10),
    id_genero INT,
    CONSTRAINT fk_videojuego_genero
        FOREIGN KEY (id_genero) REFERENCES GENERO(id_genero)
            ON UPDATE CASCADE
            ON DELETE SET NULL
);

CREATE TABLE VIDEOJUEGO_PLATAFORMA (
    id_videojuego INT NOT NULL,
    id_plataforma INT NOT NULL,
    PRIMARY KEY (id_videojuego, id_plataforma),
    CONSTRAINT fk_vs_videojuego
        FOREIGN KEY (id_videojuego) REFERENCES VIDEOJUEGO(id_videojuego)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_vs_plataforma
        FOREIGN KEY (id_plataforma) REFERENCES PLATAFORMA(id_plataforma)
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

CREATE TABLE COMPRA (
    id_compra INT PRIMARY KEY AUTO_INCREMENT,
    id_videojuego INT NOT NULL,
    fecha DATE NOT NULL,
    precio DECIMAL(10, 2),
    tienda VARCHAR(100),
    CONSTRAINT fk_compra_videojuego
        FOREIGN KEY (id_videojuego) REFERENCES VIDEOJUEGO(id_videojuego)
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

CREATE INDEX idx_videojuego_titulo ON VIDEOJUEGO(titulo);
CREATE INDEX idx_videojuego_genero ON VIDEOJUEGO(id_genero);
CREATE INDEX idx_compra_videojuego ON COMPRA(id_videojuego);