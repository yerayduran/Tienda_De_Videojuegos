package com.micoleccion.dao.impl;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.db.ConexionDB;
import com.micoleccion.model.Videojuego;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideojuegoDAOMySQL implements VideojuegoDAO {

    @Override
    public List<Videojuego> buscar(String titulo, Integer idGenero, Integer idPlataforma) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT 
                    v.id_videojuego,
                    v.titulo,
                    v.año,
                    v.nota,
                    v.url_portada,
                    GROUP_CONCAT(DISTINCT g.id_genero ORDER BY g.id_genero SEPARATOR ',') AS id_generos,
                    GROUP_CONCAT(DISTINCT g.nombre ORDER BY g.nombre SEPARATOR ', ') AS genero_nombres,
                    GROUP_CONCAT(DISTINCT p.id_plataforma ORDER BY p.id_plataforma SEPARATOR ',') AS id_plataformas,
                    GROUP_CONCAT(DISTINCT p.nombre ORDER BY p.nombre SEPARATOR ', ') AS plataforma_nombres,
                    c.fecha,
                    c.precio,
                    c.tienda
                FROM VIDEOJUEGO v
                LEFT JOIN VIDEOJUEGO_GENERO vg ON vg.id_videojuego = v.id_videojuego
                LEFT JOIN GENERO g ON g.id_genero = vg.id_genero
                LEFT JOIN VIDEOJUEGO_PLATAFORMA vp ON vp.id_videojuego = v.id_videojuego
                LEFT JOIN PLATAFORMA p ON p.id_plataforma = vp.id_plataforma
                LEFT JOIN COMPRA c ON c.id_compra = (
                     SELECT c2.id_compra
                     FROM COMPRA c2
                     WHERE c2.id_videojuego = v.id_videojuego
                     ORDER BY c2.fecha DESC, c2.id_compra DESC
                     LIMIT 1
                )
                WHERE 1=1""");

        List<Object> params = new ArrayList<>();
        if (titulo != null && !titulo.isBlank()) {
            sql.append(" AND v.titulo LIKE ?");
            params.add("%" + titulo.trim() + "%");
        }
        if (idGenero != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM VIDEOJUEGO_GENERO vgf WHERE vgf.id_videojuego = v.id_videojuego AND vgf.id_genero = ?)");
            params.add(idGenero);
        }
        if (idPlataforma != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM VIDEOJUEGO_PLATAFORMA vpf WHERE vpf.id_videojuego = v.id_videojuego AND vpf.id_plataforma = ?)");
            params.add(idPlataforma);
        }

        sql.append(" GROUP BY v.id_videojuego, c.id_compra ORDER BY v.titulo ASC");

        List<Videojuego> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Videojuego v = new Videojuego();
                    v.setIdVideojuego(rs.getInt("id_videojuego"));
                    v.setTitulo(rs.getString("titulo"));
                    v.setAño((Integer) rs.getObject("año"));
                    v.setNota((Integer) rs.getObject("nota"));
                    v.setUrlPortada(rs.getString("url_portada"));
                    v.setGenerosTexto(rs.getString("genero_nombres"));
                    v.setPlataformasTexto(rs.getString("plataforma_nombres"));

                    String idsG = rs.getString("id_generos");
                    if (idsG != null) for (String s : idsG.split(",")) v.getIdsGeneros().add(Integer.parseInt(s.trim()));

                    String idsP = rs.getString("id_plataformas");
                    if (idsP != null) for (String s : idsP.split(",")) v.getIdsPlataformas().add(Integer.parseInt(s.trim()));

                    Date d = rs.getDate("fecha");
                    if (d != null) v.setFechaCompra(d.toLocalDate());
                    v.setPrecioCompra(rs.getBigDecimal("precio"));
                    v.setTiendaCompra(rs.getString("tienda"));
                    lista.add(v);
                }
            }
        }
        return lista;
    }

    @Override
    public void insertarConCompra(Videojuego v) throws SQLException {
        String sqlV = "INSERT INTO VIDEOJUEGO (titulo, año, nota, url_portada) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = conn.prepareStatement(sqlV, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, v.getTitulo());
                    ps.setObject(2, v.getAño());
                    ps.setObject(3, v.getNota());
                    ps.setString(4, v.getUrlPortada());
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (!rs.next()) throw new SQLException("Error al obtener ID");
                    id = rs.getInt(1);
                }
                actualizarRelaciones(conn, id, v.getIdsGeneros(), v.getIdsPlataformas());

                if (v.getFechaCompra() != null) {
                    String sqlC = "INSERT INTO COMPRA (id_videojuego, fecha, precio, tienda) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlC)) {
                        ps.setInt(1, id);
                        ps.setDate(2, Date.valueOf(v.getFechaCompra()));
                        ps.setBigDecimal(3, v.getPrecioCompra());
                        ps.setString(4, v.getTiendaCompra());
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (Exception e) { conn.rollback(); throw e; }
        }
    }

    @Override
    public void actualizar(Videojuego v) throws SQLException {
        String sqlV = "UPDATE VIDEOJUEGO SET titulo=?, año=?, nota=?, url_portada=? WHERE id_videojuego=?";
        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlV)) {
                    ps.setString(1, v.getTitulo());
                    ps.setObject(2, v.getAño());
                    ps.setObject(3, v.getNota());
                    ps.setString(4, v.getUrlPortada());
                    ps.setInt(5, v.getIdVideojuego());
                    ps.executeUpdate();
                }
                // Limpiar relaciones antiguas
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM VIDEOJUEGO_GENERO WHERE id_videojuego=?")) {
                    ps.setInt(1, v.getIdVideojuego()); ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM VIDEOJUEGO_PLATAFORMA WHERE id_videojuego=?")) {
                    ps.setInt(1, v.getIdVideojuego()); ps.executeUpdate();
                }
                actualizarRelaciones(conn, v.getIdVideojuego(), v.getIdsGeneros(), v.getIdsPlataformas());
                conn.commit();
            } catch (Exception e) { conn.rollback(); throw e; }
        }
    }

    private void actualizarRelaciones(Connection conn, int idV, List<Integer> idsG, List<Integer> idsP) throws SQLException {
        if (idsG != null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO VIDEOJUEGO_GENERO (id_videojuego, id_genero) VALUES (?, ?)")) {
                for (int g : idsG) { ps.setInt(1, idV); ps.setInt(2, g); ps.addBatch(); }
                ps.executeBatch();
            }
        }
        if (idsP != null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO VIDEOJUEGO_PLATAFORMA (id_videojuego, id_plataforma) VALUES (?, ?)")) {
                for (int p : idsP) { ps.setInt(1, idV); ps.setInt(2, p); ps.addBatch(); }
                ps.executeBatch();
            }
        }
    }

    @Override public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM VIDEOJUEGO WHERE id_videojuego=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}