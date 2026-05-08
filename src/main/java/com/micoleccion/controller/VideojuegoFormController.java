package com.micoleccion.controller;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.dao.impl.VideojuegoDAOMySQL;
import com.micoleccion.model.Genero;
import com.micoleccion.model.Plataforma;
import com.micoleccion.model.Videojuego;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import org.controlsfx.control.CheckListView;
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

    private java.util.function.Consumer<javafx.scene.Node> onShowModal;
    private Runnable onCloseModal;
    private Runnable onGuardadoExitoso;

    public void setModalHandlers(java.util.function.Consumer<javafx.scene.Node> onShow, Runnable onClose) {
        this.onShowModal = onShow; this.onCloseModal = onClose;
    }
    public void setOnGuardadoExitoso(Runnable r) { this.onGuardadoExitoso = r; }
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
            validarCampos();
            Videojuego v = (original == null) ? new Videojuego() : original;
            v.setTitulo(txtTitulo.getText().trim());
            v.setAño(Integer.parseInt(txtAño.getText().trim()));
            v.setNota(Integer.parseInt(txtNota.getText().trim()));
            v.setPrecioCompra(new BigDecimal(txtPrecio.getText().trim().replace(",", ".")));
            v.setTiendaCompra(txtTienda.getText().trim());
            v.setFechaCompra(dpFechaCompra.getValue());
            v.setUrlPortada(txtUrlPortada.getText().trim());

            v.getIdsGeneros().clear();
            lvGeneros.getCheckModel().getCheckedItems().forEach(g -> v.getIdsGeneros().add(g.idGenero()));
            v.getIdsPlataformas().clear();
            lvPlataformas.getCheckModel().getCheckedItems().forEach(p -> v.getIdsPlataformas().add(p.idPlataforma()));

            if (original == null) videojuegoDAO.insertarConCompra(v);
            else videojuegoDAO.actualizar(v);

            guardado = true;
            if (onGuardadoExitoso != null) onGuardadoExitoso.run();
            onCerrar();
        } catch (IllegalArgumentException e) {
            mostrarAlertaValidacion("¡FALTAN DATOS!", e.getMessage());
        } catch (Exception e) {
            mostrarAlertaValidacion("¡HAY UN ERROR!", "Revisa que el año, la nota y el precio sean números.");
        }
    }

    private void validarCampos() {
        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) throw new IllegalArgumentException("El título del juego no puede estar vacío.");
        if (txtAño.getText() == null || txtAño.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta el año de lanzamiento.");
        if (txtNota.getText() == null || txtNota.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta la nota del 1 al 10.");
        if (txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta el precio.");
        if (txtTienda.getText() == null || txtTienda.getText().trim().isEmpty()) throw new IllegalArgumentException("Dinos en qué tienda lo compraste.");
        if (dpFechaCompra.getValue() == null) throw new IllegalArgumentException("Selecciona qué día lo compraste.");
        if (dpFechaCompra.getValue().isAfter(java.time.LocalDate.now())) throw new IllegalArgumentException("La fecha de compra no puede ser futura.");
        if (lvGeneros.getCheckModel().getCheckedItems().isEmpty()) throw new IllegalArgumentException("Selecciona un género.");
        if (lvPlataformas.getCheckModel().getCheckedItems().isEmpty()) throw new IllegalArgumentException("Selecciona una plataforma.");
    }

    private void mostrarAlertaValidacion(String titulo, String mensaje) {
        javafx.scene.control.Label lblT = new javafx.scene.control.Label(titulo);
        lblT.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-effect: dropshadow(gaussian, #ff0055, 10, 0.4, 0, 0);");

        javafx.scene.control.Label lblM = new javafx.scene.control.Label(mensaje);
        lblM.setStyle("-fx-text-fill: #a0a5b5; -fx-font-size: 14px;");
        lblM.setAlignment(javafx.geometry.Pos.CENTER); lblM.setWrapText(true); lblM.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        javafx.scene.control.Button btn = new javafx.scene.control.Button("REVISAR");
        btn.getStyleClass().addAll("button", "btn-primario");
        btn.setOnAction(e -> onCloseModal.run()); // Quita solo esta alerta

        javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(20, lblT, lblM, btn);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setStyle("-fx-background-color: #090a0f; -fx-border-color: #ff0055; -fx-border-width: 2px; -fx-padding: 30; -fx-background-radius: 12; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, #ff0055, 15, 0.3, 0, 0);");
        layout.setMaxWidth(380); layout.setMaxHeight(220);

        if (onShowModal != null) onShowModal.accept(layout);
    }

    @FXML private void onCerrar() { if (onCloseModal != null) onCloseModal.run(); }
    public boolean isGuardado() { return guardado; }

    @FXML private void onMousePressed(MouseEvent e) { xOffset = e.getSceneX(); yOffset = e.getSceneY(); }
    @FXML private void onMouseDragged(MouseEvent e) {
        javafx.stage.Window w = txtTitulo.getScene().getWindow();
        if (w instanceof javafx.stage.Stage) { w.setX(e.getScreenX() - xOffset); w.setY(e.getScreenY() - yOffset); }
    }
}