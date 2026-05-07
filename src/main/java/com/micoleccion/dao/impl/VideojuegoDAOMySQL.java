/**
 * @author ManuelPerez
 * @version 1.0
 */

package com.micoleccion.dao.impl;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.db.ConexionDB;
import com.micoleccion.model.Videojuego;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VideojuegoDAOMySQL implements VideojuegoDAO {

    @Override
    public List<Videojuego> buscar(String titulo, Integer idGenero, Integer idPlataforma) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT v.id_videojuego,
                       v.titulo,
                       v.año,
                       v.nota,
                       g.id_genero,
                       g.nombre AS genero_nombre,
                       p.id_plataforma,
                       p.nombre AS plataforma_nombre,
                       c.fecha,
                       c.precio,
                       c.tienda
                FROM VIDEOJUEGO v
                LEFT JOIN GENERO g ON g.id_genero = v.id_genero
                LEFT JOIN VIDEOJUEGO_PLATAFORMA vp ON vp.id_videojuego = v.id_videojuego
                LEFT JOIN PLATAFORMA p ON p.id_plataforma = vp.id_plataforma
                LEFT JOIN COMPRA c ON c.id_compra = (
                     SELECT c2.id_compra
                     FROM COMPRA c2
                     WHERE c2.id_videojuego = v.id_videojuego
                     ORDER BY c2.fecha DESC, c2.id_compra DESC
                     LIMIT 1
                )
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();
        if (titulo != null && !titulo.isBlank()) {
            sql.append(" AND v.titulo LIKE ?");
            params.add("%" + titulo.trim() + "%");
        }
        if (idGenero != null) {
            sql.append(" AND v.id_genero = ?");
            params.add(idGenero);
        }
        if (idPlataforma != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM VIDEOJUEGO_PLATAFORMA vpf WHERE vpf.id_videojuego = v.id_videojuego AND vpf.id_plataforma = ?)");
            params.add(idPlataforma);
        }
        sql.append(" ORDER BY v.titulo ASC, p.nombre ASC");

        Map<Integer, Videojuego> mapa = new LinkedHashMap<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_videojuego");
                    Videojuego videojuego = mapa.get(id);
                    if (videojuego == null) {
                        videojuego = new Videojuego();
                        videojuego.setIdVideojuego(id);
                        videojuego.setTitulo(rs.getString("titulo"));
                        videojuego.setAño((Integer) rs.getObject("año"));
                        videojuego.setNota((Integer) rs.getObject("nota"));
                        videojuego.setIdGenero((Integer) rs.getObject("id_genero"));
                        videojuego.setNombreGenero(rs.getString("genero_nombre"));

                        Date fecha = rs.getDate("fecha");
                        if (fecha != null) {
                            videojuego.setFechaCompra(fecha.toLocalDate());
                        }
                        videojuego.setPrecioCompra(rs.getBigDecimal("precio"));
                        videojuego.setTiendaCompra(rs.getString("tienda"));

                        mapa.put(id, videojuego);
                    }
                    Integer idPlataformaFila = (Integer) rs.getObject("id_plataforma");
                    String nombrePlataforma = rs.getString("plataforma_nombre");
                    if (idPlataformaFila != null && nombrePlataforma != null
                            && !videojuego.getIdsPlataformas().contains(idPlataformaFila)) {
                        videojuego.getIdsPlataformas().add(idPlataformaFila);
                        if (videojuego.getPlataformasTexto() == null || videojuego.getPlataformasTexto().isBlank()) {
                            videojuego.setPlataformasTexto(nombrePlataforma);
                        } else {
                            videojuego.setPlataformasTexto(videojuego.getPlataformasTexto() + ", " + nombrePlataforma);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }

    @Override
    public void insertarConCompra(Videojuego videojuego) throws SQLException {
        String sqlInsertVideojuego = "INSERT INTO VIDEOJUEGO (titulo, año, nota, id_genero) VALUES (?, ?, ?, ?)";
        String sqlInsertRelacion = "INSERT INTO VIDEOJUEGO_PLATAFORMA (id_videojuego, id_plataforma) VALUES (?, ?)";
        String sqlInsertCompra = "INSERT INTO COMPRA (id_videojuego, fecha, precio, tienda) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idVideojuego;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertVideojuego, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, videojuego.getTitulo());
                    if (videojuego.getAño() == null) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, videojuego.getAño());
                    }
                    if (videojuego.getNota() == null) {
                        ps.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(3, videojuego.getNota());
                    }
                    if (videojuego.getIdGenero() == null) {
                        ps.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(4, videojuego.getIdGenero());
                    }
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo recuperar el ID generado.");
                        }
                        idVideojuego = keys.getInt(1);
                    }
                }

                if (videojuego.getIdsPlataformas() != null && !videojuego.getIdsPlataformas().isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertRelacion)) {
                        for (Integer idPlataforma : videojuego.getIdsPlataformas()) {
                            ps.setInt(1, idVideojuego);
                            ps.setInt(2, idPlataforma);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                if (videojuego.getFechaCompra() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertCompra)) {
                        ps.setInt(1, idVideojuego);
                        ps.setDate(2, Date.valueOf(videojuego.getFechaCompra()));
                        ps.setBigDecimal(3, videojuego.getPrecioCompra());
                        ps.setString(4, videojuego.getTiendaCompra());
                        ps.executeUpdate();
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw new SQLException("Transaccion revertida: " + ex.getMessage(), ex);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizar(Videojuego videojuego) throws SQLException {
        String sqlUpdate = "UPDATE VIDEOJUEGO SET titulo = ?, año = ?, nota = ?, id_genero = ? WHERE id_videojuego = ?";
        String sqlDeleteRelaciones = "DELETE FROM VIDEOJUEGO_PLATAFORMA WHERE id_videojuego = ?";
        String sqlInsertRelacion = "INSERT INTO VIDEOJUEGO_PLATAFORMA (id_videojuego, id_plataforma) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, videojuego.getTitulo());
                    if (videojuego.getAño() == null) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, videojuego.getAño());
                    }
                    if (videojuego.getNota() == null) {
                        ps.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(3, videojuego.getNota());
                    }
                    if (videojuego.getIdGenero() == null) {
                        ps.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(4, videojuego.getIdGenero());
                    }
                    ps.setInt(5, videojuego.getIdVideojuego());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteRelaciones)) {
                    ps.setInt(1, videojuego.getIdVideojuego());
                    ps.executeUpdate();
                }

                if (videojuego.getIdsPlataformas() != null && !videojuego.getIdsPlataformas().isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertRelacion)) {
                        for (Integer idPlataforma : videojuego.getIdsPlataformas()) {
                            ps.setInt(1, videojuego.getIdVideojuego());
                            ps.setInt(2, idPlataforma);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw new SQLException("Error al actualizar; rollback ejecutado.", ex);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void eliminar(int idVideojuego) throws SQLException {
        String sql = "DELETE FROM VIDEOJUEGO WHERE id_videojuego = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVideojuego);
            ps.executeUpdate();
        }
    }
}