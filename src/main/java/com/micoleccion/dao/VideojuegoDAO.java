package com.micoleccion.dao;

import com.micoleccion.model.Videojuego;

import java.sql.SQLException;
import java.util.List;

public interface VideojuegoDAO {
    List<Videojuego> buscar(String titulo, Integer idGenero, Integer idPlataforma) throws SQLException;

    void insertarConCompra(Videojuego videojuego) throws SQLException;

    void actualizar(Videojuego videojuego) throws SQLException;

    void eliminar(int idVideojuego) throws SQLException;
}