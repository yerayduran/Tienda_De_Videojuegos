package com.micoleccion.dao.impl;

import com.micoleccion.dao.GeneroDAO;
import com.micoleccion.db.ConexionDB;
import com.micoleccion.model.Genero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAOMySQL implements GeneroDAO {

    @Override
    public List<Genero> listarTodos() throws SQLException {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT id_genero, nombre FROM GENERO ORDER BY nombre";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                generos.add(new Genero(
                        rs.getInt("id_genero"),
                        rs.getString("nombre")
                ));
            }
        }

        return generos;
    }
}