package com.micoleccion.controller;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.dao.impl.VideojuegoDAOMySQL;
import com.micoleccion.model.Genero;
import com.micoleccion.model.Plataforma;
import com.micoleccion.model.Videojuego;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VideojuegoFormController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAño;
    @FXML private TextField txtNota;
    @FXML private ComboBox<Genero> cbGenero;
    @FXML private ListView<Plataforma> lvPlataformas;
    @FXML private DatePicker dpFechaCompra;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtTienda;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOMySQL();
    private Videojuego original;
    private boolean guardado;

    @FXML
    private void initialize() {
        lvPlataformas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (btnGuardar != null && !btnGuardar.getStyleClass().contains("btn-primario")) {
            btnGuardar.getStyleClass().add("btn-primario");
        }

        if (btnCancelar != null && !btnCancelar.getStyleClass().contains("btn-secundario")) {
            btnCancelar.getStyleClass().add("btn-secundario");
        }

        Platform.runLater(() -> {
            if (txtTitulo.getScene() != null) {
                try {
                    String cssUrl = getClass().getResource("/com/micoleccion/css/VideojuegoFormController.css").toExternalForm();

                    if (!txtTitulo.getScene().getStylesheets().contains(cssUrl)) {
                        txtTitulo.getScene().getStylesheets().add(cssUrl);
                    }

                } catch (Exception e) {
                    System.out.println("CUIDADO: No se encontró VideojuegoFormController.css en la ruta indicada.");
                }

                Parent root = txtTitulo.getScene().getRoot();
                if (root != null && !root.getStyleClass().contains("root-pane")) {
                    root.getStyleClass().add("root-pane");
                }

                if (root != null) {
                    for (Node node : root.lookupAll(".label")) {
                        if (node instanceof Label && !node.getStyleClass().contains("label")) {
                            node.getStyleClass().add("label");
                        }
                    }
                }
            }
        });
    }

    public void setDatos(Videojuego videojuego, List<Genero> generos, List<Plataforma> plataformas) {
        this.original = videojuego;
        cbGenero.getItems().setAll(generos);
        lvPlataformas.getItems().setAll(plataformas);

        if (videojuego != null) {
            txtTitulo.setText(videojuego.getTitulo());

            if (videojuego.getAño() != null) {
                txtAño.setText(String.valueOf(videojuego.getAño()));
            }

            if (videojuego.getNota() != null) {
                txtNota.setText(String.valueOf(videojuego.getNota()));
            }

            if (videojuego.getIdGenero() != null) {
                for (Genero g : generos) {
                    if (g.idGenero().equals(videojuego.getIdGenero())) {
                        cbGenero.setValue(g);
                        break;
                    }
                }
            }

            List<Integer> idsPlataformas = videojuego.getIdsPlataformas();
            if (idsPlataformas == null) {
                idsPlataformas = new ArrayList<>();
            }

            for (Plataforma p : plataformas) {
                if (idsPlataformas.contains(p.idPlataforma())) {
                    lvPlataformas.getSelectionModel().select(p);
                }
            }

            dpFechaCompra.setValue(videojuego.getFechaCompra());

            if (videojuego.getPrecioCompra() != null) {
                txtPrecio.setText(videojuego.getPrecioCompra().toPlainString());
            }

            txtTienda.setText(videojuego.getTiendaCompra());
        }
    }

    @FXML
    private void onGuardar() {
        try {
            Videojuego v = validarYConstruir();

            if (original == null) {
                videojuegoDAO.insertarConCompra(v);
            } else {
                v.setIdVideojuego(original.getIdVideojuego());
                videojuegoDAO.actualizar(v);
            }

            guardado = true;
            cerrar();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error SQL: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

    public boolean isGuardado() {
        return guardado;
    }

    private Videojuego validarYConstruir() {
        String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            throw new IllegalArgumentException("El titulo es obligatorio.");
        }

        Integer año = null;
        if (txtAño.getText() != null && !txtAño.getText().isBlank()) {
            año = Integer.parseInt(txtAño.getText().trim());
        }

        Integer nota = null;
        if (txtNota.getText() != null && !txtNota.getText().isBlank()) {
            nota = Integer.parseInt(txtNota.getText().trim());
            if (nota < 1 || nota > 10) {
                throw new IllegalArgumentException("La nota debe estar entre 1 y 10.");
            }
        }

        BigDecimal precio = null;
        if (txtPrecio.getText() != null && !txtPrecio.getText().isBlank()) {
            precio = new BigDecimal(txtPrecio.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo.");
            }
        }

        List<Integer> idsPlataformas = new ArrayList<>();
        for (Plataforma plataforma : lvPlataformas.getSelectionModel().getSelectedItems()) {
            idsPlataformas.add(plataforma.idPlataforma());
        }

        Videojuego v = new Videojuego();
        v.setTitulo(titulo);
        v.setAño(año);
        v.setNota(nota);
        v.setIdGenero(cbGenero.getValue() == null ? null : cbGenero.getValue().idGenero());
        v.setIdsPlataformas(idsPlataformas);
        v.setFechaCompra(dpFechaCompra.getValue());
        v.setPrecioCompra(precio);
        v.setTiendaCompra(txtTienda.getText() == null ? null : txtTienda.getText().trim());

        return v;
    }

    private void cerrar() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Validacion");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}