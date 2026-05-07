package com.micoleccion.dao.impl;

import com.micoleccion.dao.PlataformaDAO;
import com.micoleccion.db.ConexionDB;
import com.micoleccion.model.Plataforma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlataformaDAOMySQL implements PlataformaDAO {

    @Override
    public List<Plataforma> listarTodas() throws SQLException {
        List<Plataforma> plataformas = new ArrayList<>();
        String sql = "SELECT id_plataforma, nombre FROM PLATAFORMA ORDER BY nombre";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                plataformas.add(new Plataforma(rs.getInt("id_plataforma"), rs.getString("nombre")));
            }
        }
        return plataformas;
    }
}