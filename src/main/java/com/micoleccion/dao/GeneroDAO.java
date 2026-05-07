package com.micoleccion.dao;

import com.micoleccion.model.Genero;

import java.sql.SQLException;
import java.util.List;

public interface GeneroDAO {
    List<Genero> listarTodos() throws SQLException;
}