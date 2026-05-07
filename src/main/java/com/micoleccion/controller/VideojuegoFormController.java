package com.micoleccion.controller;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.dao.impl.VideojuegoDAOMySQL;
import com.micoleccion.model.Genero;
import com.micoleccion.model.Plataforma;
import com.micoleccion.model.Videojuego;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView; // Import correcto
import java.math.BigDecimal;
import java.util.List;

public class VideojuegoFormController {

    @FXML private TextField txtTitulo, txtAño, txtNota, txtPrecio, txtTienda, txtUrlPortada;
    @FXML private CheckListView<Genero> lvGeneros; // Coincide con FXML
    @FXML private CheckListView<Plataforma> lvPlataformas; // Coincide con FXML
    @FXML private DatePicker dpFechaCompra;

    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOMySQL();
    private Videojuego original;
    private boolean guardado = false;
    private double xOffset, yOffset;

    public void setDatos(Videojuego v, List<Genero> todosG, List<Plataforma> todasP) {
        this.original = v;
        lvGeneros.getItems().setAll(todosG);
        lvPlataformas.getItems().setAll(todasP);

        if (v != null) {
            txtTitulo.setText(v.getTitulo());
            txtAño.setText(v.getAño() != null ? String.valueOf(v.getAño()) : "");
            txtNota.setText(v.getNota() != null ? String.valueOf(v.getNota()) : "");
            txtPrecio.setText(v.getPrecioCompra() != null ? v.getPrecioCompra().toString() : "");
            txtTienda.setText(v.getTiendaCompra());
            txtUrlPortada.setText(v.getUrlPortada());
            dpFechaCompra.setValue(v.getFechaCompra());

            // Marcar checks existentes
            lvGeneros.getItems().forEach(g -> {
                if (v.getIdsGeneros().contains(g.idGenero())) lvGeneros.getCheckModel().check(g);
            });
            lvPlataformas.getItems().forEach(p -> {
                if (v.getIdsPlataformas().contains(p.idPlataforma())) lvPlataformas.getCheckModel().check(p);
            });
        }
    }

    @FXML
    private void onGuardar() {
        try {
            Videojuego v = (original == null) ? new Videojuego() : original;
            v.setTitulo(txtTitulo.getText());
            v.setAño(txtAño.getText().isBlank() ? null : Integer.parseInt(txtAño.getText()));
            v.setNota(txtNota.getText().isBlank() ? null : Integer.parseInt(txtNota.getText()));
            v.setPrecioCompra(txtPrecio.getText().isBlank() ? BigDecimal.ZERO : new BigDecimal(txtPrecio.getText()));
            v.setTiendaCompra(txtTienda.getText());
            v.setFechaCompra(dpFechaCompra.getValue());
            v.setUrlPortada(txtUrlPortada.getText());

            v.getIdsGeneros().clear();
            lvGeneros.getCheckModel().getCheckedItems().forEach(g -> v.getIdsGeneros().add(g.idGenero()));
            v.getIdsPlataformas().clear();
            lvPlataformas.getCheckModel().getCheckedItems().forEach(p -> v.getIdsPlataformas().add(p.idPlataforma()));

            if (original == null) videojuegoDAO.insertarConCompra(v);
            else videojuegoDAO.actualizar(v);

            guardado = true;
            onCerrar();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void onCerrar() { ((Stage) txtTitulo.getScene().getWindow()).close(); }
    public boolean isGuardado() { return guardado; }
    @FXML private void onMousePressed(MouseEvent e) { xOffset = e.getSceneX(); yOffset = e.getSceneY(); }
    @FXML private void onMouseDragged(MouseEvent e) {
        Stage s = (Stage) txtTitulo.getScene().getWindow();
        s.setX(e.getScreenX() - xOffset); s.setY(e.getScreenY() - yOffset);
    }
}