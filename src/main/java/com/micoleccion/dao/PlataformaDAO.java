package com.micoleccion.dao;

import com.micoleccion.model.Plataforma;

import java.sql.SQLException;
import java.util.List;

public interface PlataformaDAO {
    List<Plataforma> listarTodas() throws SQLException;
}