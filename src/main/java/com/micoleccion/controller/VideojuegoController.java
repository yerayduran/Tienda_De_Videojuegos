package com.micoleccion.controller;

import com.micoleccion.utils.AnimationUtils;
import javafx.scene.control.TableRow;
import com.micoleccion.MainApp;
import com.micoleccion.dao.GeneroDAO;
import com.micoleccion.dao.PlataformaDAO;
import com.micoleccion.dao.VideojuegoDAO;
import com.micoleccion.dao.impl.GeneroDAOMySQL;
import com.micoleccion.dao.impl.PlataformaDAOMySQL;
import com.micoleccion.dao.impl.VideojuegoDAOMySQL;
import com.micoleccion.model.Genero;
import com.micoleccion.model.Plataforma;
import com.micoleccion.model.Videojuego;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VideojuegoController {

    @FXML private TableView<Videojuego> tvVideojuegos;
    @FXML private TableColumn<Videojuego, Integer> colId;
    @FXML private TableColumn<Videojuego, String> colTitulo;
    @FXML private TableColumn<Videojuego, Integer> colAño;
    @FXML private TableColumn<Videojuego, Integer> colNota;
    @FXML private TableColumn<Videojuego, String> colPortada;
    @FXML private TableColumn<Videojuego, String> colGenero;
    @FXML private TableColumn<Videojuego, String> colPlataformas;
    @FXML private TableColumn<Videojuego, String> colFechaCompra;
    @FXML private TableColumn<Videojuego, String> colPrecio;

    @FXML private TextField txtBuscarTitulo;
    @FXML private ComboBox<Genero> cbGeneroFiltro;
    @FXML private ComboBox<Plataforma> cbPlataformaFiltro;
    @FXML private Label lblEstado;
    @FXML private javafx.scene.layout.StackPane overlayPane;
    @FXML private javafx.scene.layout.BorderPane mainContent;
    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOMySQL();
    private final GeneroDAO generoDAO = new GeneroDAOMySQL();
    private final PlataformaDAO plataformaDAO = new PlataformaDAOMySQL();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        // Configuración visual de la tabla
        tvVideojuegos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- INYECTAR ANIMACIÓN BLUR & POP EN LA TABLA ---
        tvVideojuegos.setRowFactory(tv -> {
            TableRow<Videojuego> row = new TableRow<>();
            AnimationUtils.applyRowFocusEffect(tv, row);
            return row;
        });

        // ... (Mantén tus mapeos de columnas intactos aquí) ...
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdVideojuego()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAño.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAño()));
        colNota.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNota()));
        colGenero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenerosTexto() == null ? "-" : data.getValue().getGenerosTexto()));
        colPlataformas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlataformasTexto() == null ? "-" : data.getValue().getPlataformasTexto()));
        colFechaCompra.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaCompra() == null ? "-" : data.getValue().getFechaCompra().format(formatter)));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrecioCompra() == null ? "-" : data.getValue().getPrecioCompra() + " €"));

        colPortada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrlPortada()));
        colPortada.setCellFactory(col -> new TableCell<Videojuego, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(90);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                imageView.setImage(cargarImagen(item));
                setGraphic(imageView);
            }
        });

        cargarCatalogos();

        // Diferir la carga inicial y aplicar animaciones a la ventana
        Platform.runLater(() -> {
            buscarYRefrescar();

            // Animación de aparición de la ventana completa
            Parent root = tvVideojuegos.getScene().getRoot();
            if (root != null) {
                AnimationUtils.fadeIn(root);
                // Animación de rebote a todos los botones
                root.lookupAll(".btn-primario").forEach(AnimationUtils::applyBouncingEffect);
                root.lookupAll(".btn-secundario").forEach(AnimationUtils::applyBouncingEffect);
                root.lookupAll(".btn-peligro").forEach(AnimationUtils::applyBouncingEffect);
            }
        });
    }

    private Image cargarImagen(String ruta) {
        String fallback = "/images/sin_portada.png";
        try {
            if (ruta == null || ruta.isBlank()) {
                return new Image(MainApp.class.getResource(fallback).toExternalForm(), true);
            }

            String normalizada = ruta.trim();
            if (normalizada.startsWith("http://") || normalizada.startsWith("https://") || normalizada.startsWith("file:")) {
                return new Image(normalizada, true);
            }

            File archivo = new File(normalizada);
            if (archivo.exists()) {
                return new Image(archivo.toURI().toString(), true);
            }

            String classpathPath = normalizada.startsWith("/") ? normalizada : "/" + normalizada;
            var resource = MainApp.class.getResource(classpathPath);
            if (resource != null) {
                return new Image(resource.toExternalForm(), true);
            }
        } catch (Exception ignored) {
        }

        try {
            return new Image(MainApp.class.getResource(fallback).toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen de fallback: " + e.getMessage());
            return null;
        }
    }

    private void cargarCatalogos() {
        try {
            List<Genero> generos = generoDAO.listarTodos();
            List<Plataforma> plataformas = plataformaDAO.listarTodas();

            cbGeneroFiltro.setItems(FXCollections.observableArrayList(generos));
            cbPlataformaFiltro.setItems(FXCollections.observableArrayList(plataformas));
        } catch (SQLException e) {
            System.err.println("Error cargando catálogos: " + e.getMessage());
        }
    }


    @FXML
    private void onBuscar() {
        buscarYRefrescar();
    }

    @FXML
    private void onLimpiarFiltros() {
        txtBuscarTitulo.clear();

        // Forzamos la limpieza y reinstauramos el texto del placeholder
        cbGeneroFiltro.getSelectionModel().clearSelection();
        cbGeneroFiltro.setValue(null);
        cbGeneroFiltro.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Genero item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? cbGeneroFiltro.getPromptText() : item.toString());
            }
        });

        cbPlataformaFiltro.getSelectionModel().clearSelection();
        cbPlataformaFiltro.setValue(null);
        cbPlataformaFiltro.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Plataforma item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? cbPlataformaFiltro.getPromptText() : item.toString());
            }
        });

        buscarYRefrescar();
    }

    private void buscarYRefrescar() {
        try {
            String titulo = txtBuscarTitulo.getText().trim();
            if (titulo.isEmpty()) {
                titulo = null;
            }
            Genero genero = cbGeneroFiltro.getValue();
            Plataforma plataforma = cbPlataformaFiltro.getValue();

            List<Videojuego> lista = videojuegoDAO.buscar(
                    titulo,
                    genero == null ? null : genero.idGenero(),
                    plataforma == null ? null : plataforma.idPlataforma()
            );

            tvVideojuegos.setItems(FXCollections.observableArrayList(lista));
            lblEstado.setText(lista.size() + " videojuegos encontrados");
        } catch (SQLException e) {
            mostrarError("Error en la búsqueda.", e);
        }
    }

    @FXML
    private void onNuevo() {
        abrirFormulario(null);
    }

    @FXML
    private void onEditar() {
        Videojuego seleccionado = tvVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarInfoPersonalizada("¡UN MOMENTO!", "Selecciona un juego de la tabla para editar.");            return;
        }
        abrirFormulario(seleccionado);
    }

    private void abrirFormulario(Videojuego v) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/micoleccion/view/videojuego-form-view.fxml"));
            Parent root = loader.load();
            VideojuegoFormController controller = loader.getController();
            controller.setDatos(v, generoDAO.listarTodos(), plataformaDAO.listarTodas());

            // Conectamos el form a nuestra capa de Overlays y TOASTS
            controller.setModalHandlers(this::mostrarModal, this::cerrarModal);
            controller.setToastHandler((titulo, msg) -> com.micoleccion.utils.AnimationUtils.showToast(overlayPane, titulo, msg, "#ff0055"));
            controller.setOnGuardadoExitoso(this::buscarYRefrescar);

            ((javafx.scene.layout.VBox) root).setMaxSize(520, 750);
            mostrarModal(root);
        } catch (Exception e) {
            mostrarError("No se pudo abrir el formulario", e);
        }
    }
    @FXML
    private void onEliminar() {
        Videojuego seleccionado = tvVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlertaFlotante("¡UN MOMENTO!", "Selecciona un juego de la tabla para eliminar.", "#00f0ff");
            return;
        }

        javafx.scene.control.Label lblT = new javafx.scene.control.Label("¿ELIMINAR JUEGO?");
        lblT.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-effect: dropshadow(gaussian, #ff0055, 10, 0.4, 0, 0);");

        javafx.scene.control.Label lblM = new javafx.scene.control.Label("Estás a punto de borrar '" + seleccionado.getTitulo() + "'. Esta acción no se puede deshacer. ¿Continuar?");
        lblM.setStyle("-fx-text-fill: #a0a5b5; -fx-font-size: 14px;");
        lblM.setWrapText(true); lblM.setAlignment(javafx.geometry.Pos.CENTER); lblM.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        javafx.scene.control.Button btnSi = new javafx.scene.control.Button("Eliminar");
        btnSi.getStyleClass().addAll("button", "btn-peligro");
        btnSi.setOnAction(e -> {
            try {
                videojuegoDAO.eliminar(seleccionado.getIdVideojuego());
                cerrarModal();
                buscarYRefrescar();
            } catch (SQLException ex) {
                mostrarError("No se pudo eliminar de la base de datos.", ex);
            }
        });

        javafx.scene.control.Button btnNo = new javafx.scene.control.Button("Cancelar");
        btnNo.getStyleClass().addAll("button", "btn-secundario");
        btnNo.setOnAction(e -> cerrarModal());

        javafx.scene.layout.HBox botones = new javafx.scene.layout.HBox(15, btnNo, btnSi);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(20, lblT, lblM, botones);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setStyle("-fx-background-color: #090a0f; -fx-border-color: #ff0055; -fx-border-width: 2px; -fx-padding: 30; -fx-background-radius: 12; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, #ff0055, 15, 0.3, 0, 0);");
        layout.setMaxWidth(400); layout.setMaxHeight(220);

        mostrarModal(layout);
    }

    @FXML private void onRecargar() { buscarYRefrescar(); }
    private void mostrarInfoPersonalizada(String titulo, String msg) { mostrarAlertaFlotante(titulo, msg, "#00f0ff"); }
    private void mostrarError(String msg, Exception e) { mostrarAlertaFlotante("¡UPS! ALGO FALLÓ", msg + "\n" + (e.getMessage() != null ? e.getMessage() : ""), "#ff0055"); }

    // --- EL SISTEMA DE MODALES IN-APP ---
// --- EL SISTEMA DE MODALES Y TOASTS ---
    private void mostrarAlertaFlotante(String titulo, String msg, String colorBorde) {
        // En lugar de crear un Modal con un botón "Entendido", ahora usamos el Toast moderno
        if (!overlayPane.isVisible()) mainContent.setEffect(new javafx.scene.effect.GaussianBlur(12));
        com.micoleccion.utils.AnimationUtils.showToast(overlayPane, titulo, msg, colorBorde);
    }
    public void mostrarModal(javafx.scene.Node modal) {
        if (!overlayPane.isVisible()) mainContent.setEffect(new javafx.scene.effect.GaussianBlur(12));
        com.micoleccion.utils.AnimationUtils.showModal(overlayPane, modal);
    }

    public void cerrarModal() {
        com.micoleccion.utils.AnimationUtils.closeModal(overlayPane);
        if (overlayPane.getChildren().size() <= 1) mainContent.setEffect(null);
    }

    @FXML private void onMousePressed(MouseEvent event) { xOffset = event.getSceneX(); yOffset = event.getSceneY(); }
    @FXML private void onMouseDragged(MouseEvent event) {
        Stage stage = (Stage) tvVideojuegos.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset); stage.setY(event.getScreenY() - yOffset);
    }
    @FXML private void onMinimizar() { ((Stage) tvVideojuegos.getScene().getWindow()).setIconified(true); }
    @FXML private void onCerrar() { Platform.exit(); }
}