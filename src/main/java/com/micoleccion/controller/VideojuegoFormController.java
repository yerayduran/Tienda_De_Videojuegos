package com.micoleccion.controller;

import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.dao.impl.VideojuegoDAOMySQL;
import com.micoleccion.model.Genero;
import com.micoleccion.model.Plataforma;
import com.micoleccion.model.Videojuego;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.math.BigDecimal;
import java.util.List;

public class VideojuegoFormController {

    @FXML private TextField txtTitulo, txtAño, txtNota, txtPrecio, txtTienda, txtUrlPortada;
    @FXML private FlowPane fpGeneros;
    @FXML private FlowPane fpPlataformas;
    @FXML private DatePicker dpFechaCompra;

    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOMySQL();
    private Videojuego original;
    private boolean guardado = false;
    private double xOffset, yOffset;

    private java.util.function.Consumer<javafx.scene.Node> onShowModal;
    private Runnable onCloseModal;
    private Runnable onGuardadoExitoso;
    private java.util.function.BiConsumer<String, String> onShowToast;

    public void setModalHandlers(java.util.function.Consumer<javafx.scene.Node> onShow, Runnable onClose) {
        this.onShowModal = onShow;
        this.onCloseModal = onClose;
    }

    public void setToastHandler(java.util.function.BiConsumer<String, String> onShowToast) {
        this.onShowToast = onShowToast;
    }

    public void setOnGuardadoExitoso(Runnable r) {
        this.onGuardadoExitoso = r;
    }

    public void setDatos(Videojuego v, List<Genero> todosG, List<Plataforma> todasP) {
        this.original = v;

        fpGeneros.getChildren().clear();
        for (Genero g : todosG) {
            ToggleButton chip = new ToggleButton(g.nombre());
            chip.setUserData(g.idGenero());
            chip.getStyleClass().add("chip-toggle");
            if (v != null && v.getIdsGeneros().contains(g.idGenero())) chip.setSelected(true);
            fpGeneros.getChildren().add(chip);
        }

        fpPlataformas.getChildren().clear();
        for (Plataforma p : todasP) {
            ToggleButton chip = new ToggleButton(p.nombre());
            chip.setUserData(p.idPlataforma());
            chip.getStyleClass().add("chip-toggle");
            if (v != null && v.getIdsPlataformas().contains(p.idPlataforma())) chip.setSelected(true);
            fpPlataformas.getChildren().add(chip);
        }

        if (v != null) {
            txtTitulo.setText(v.getTitulo());
            txtAño.setText(v.getAño() != null ? String.valueOf(v.getAño()) : "");
            txtNota.setText(v.getNota() != null ? String.valueOf(v.getNota()) : "");
            txtPrecio.setText(v.getPrecioCompra() != null ? v.getPrecioCompra().toString() : "");
            txtTienda.setText(v.getTiendaCompra());
            txtUrlPortada.setText(v.getUrlPortada());
            dpFechaCompra.setValue(v.getFechaCompra());
        }
    }

    @FXML
    private void onGuardar() {
        try {
            validarCampos();

            Videojuego v = (original == null) ? new Videojuego() : original;
            v.setTitulo(txtTitulo.getText().trim());
            v.setAño(Integer.parseInt(txtAño.getText().trim()));

            int nota = Integer.parseInt(txtNota.getText().trim());
            if (nota < 1 || nota > 10) throw new IllegalArgumentException("La nota tiene que ser un número del 1 al 10.");
            v.setNota(nota);

            v.setPrecioCompra(new BigDecimal(txtPrecio.getText().trim().replace(",", ".")));
            v.setTiendaCompra(txtTienda.getText().trim());
            v.setFechaCompra(dpFechaCompra.getValue());
            v.setUrlPortada(txtUrlPortada.getText().trim());

            v.getIdsGeneros().clear();
            for (Node n : fpGeneros.getChildren()) {
                ToggleButton tb = (ToggleButton) n;
                if (tb.isSelected()) {
                    v.getIdsGeneros().add((Integer) tb.getUserData());
                }
            }

            v.getIdsPlataformas().clear();
            for (Node n : fpPlataformas.getChildren()) {
                ToggleButton tb = (ToggleButton) n;
                if (tb.isSelected()) {
                    v.getIdsPlataformas().add((Integer) tb.getUserData());
                }
            }

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
    @FXML
    private void initialize() {
        // --- MAGIA: DRAG & DROP PARA LA PORTADA ---

        // 1. Detectar cuando el usuario arrastra un archivo por encima del campo
        txtUrlPortada.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                txtUrlPortada.setStyle("-fx-border-color: #23a559; -fx-background-color: #1a1b1e; -fx-effect: dropshadow(gaussian, #23a559, 10, 0.4, 0, 0);"); // Brillo verde al pasar por encima
            }
            event.consume();
        });

        // 2. Quitar el brillo si el usuario se arrepiente y saca el ratón de la zona
        txtUrlPortada.setOnDragExited(event -> {
            txtUrlPortada.setStyle(""); // Vuelve al CSS original
            event.consume();
        });

        // 3. Soltar el archivo y capturar la ruta
        txtUrlPortada.setOnDragDropped(event -> {
            javafx.scene.input.Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                java.io.File file = db.getFiles().get(0);
                txtUrlPortada.setText(file.toURI().toString());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // --- AUTO-COMPLETADO INTELIGENTE ---
        // Si el usuario elige una fecha, extraemos el año automáticamente y lo escribimos por él
        dpFechaCompra.valueProperty().addListener((obs, viejaFecha, nuevaFecha) -> {
            if (nuevaFecha != null && (txtAño.getText() == null || txtAño.getText().isBlank())) {
                txtAño.setText(String.valueOf(nuevaFecha.getYear()));
            }
        });
    }
    private void validarCampos() {
        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) throw new IllegalArgumentException("El título del juego no puede estar vacío.");
        if (txtAño.getText() == null || txtAño.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta el año de lanzamiento.");
        if (txtNota.getText() == null || txtNota.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta la nota del 1 al 10.");
        if (txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) throw new IllegalArgumentException("Falta el precio.");
        if (txtTienda.getText() == null || txtTienda.getText().trim().isEmpty()) throw new IllegalArgumentException("Dinos en qué tienda lo compraste.");
        if (dpFechaCompra.getValue() == null) throw new IllegalArgumentException("Selecciona qué día lo compraste.");
        if (dpFechaCompra.getValue().isAfter(java.time.LocalDate.now())) throw new IllegalArgumentException("La fecha de compra no puede ser futura.");

        boolean generoSeleccionado = fpGeneros.getChildren().stream().anyMatch(n -> ((ToggleButton) n).isSelected());
        if (!generoSeleccionado) throw new IllegalArgumentException("Selecciona por lo menos un género pulsando las burbujas.");

        boolean platSeleccionada = fpPlataformas.getChildren().stream().anyMatch(n -> ((ToggleButton) n).isSelected());
        if (!platSeleccionada) throw new IllegalArgumentException("Indica en qué plataforma juegas seleccionando su burbuja.");
    }

    private void mostrarAlertaValidacion(String titulo, String mensaje) {
        if (onShowToast != null) {
            onShowToast.accept(titulo, mensaje);
        }
    }
    @FXML
    private void onSubirImagen() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleccionar Portada Local");

        // Filtramos para que solo deje elegir fotos
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.webp", "*.gif")
        );

        // Abre la ventana de Windows para elegir archivo
        java.io.File archivo = fileChooser.showOpenDialog(txtTitulo.getScene().getWindow());

        // Si el usuario elige una foto (y no le da a cancelar)
        if (archivo != null) {
            // Convierte la ruta de tu PC a un formato URL (file://...) y la pega en el campo
            txtUrlPortada.setText(archivo.toURI().toString());
        }
    }

    @FXML private void onCerrar() { if (onCloseModal != null) onCloseModal.run(); }    public boolean isGuardado() { return guardado; }

    @FXML private void onMousePressed(MouseEvent e) { xOffset = e.getSceneX(); yOffset = e.getSceneY(); }
    @FXML private void onMouseDragged(MouseEvent e) {
        javafx.stage.Window w = txtTitulo.getScene().getWindow();
        if (w instanceof javafx.stage.Stage) { w.setX(e.getScreenX() - xOffset); w.setY(e.getScreenY() - yOffset); }
    }
}