package com.micoleccion.controller;

import com.micoleccion.utils.AnimationUtils;
import javafx.scene.Node;
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
import javafx.scene.layout.StackPane;
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

        // --- INYECTAR ANIMACIÓN BLUR, DOBLE CLIC Y MENÚ CONTEXTUAL ---
        tvVideojuegos.setRowFactory(tv -> {
            TableRow<Videojuego> row = new TableRow<>();
            com.micoleccion.utils.AnimationUtils.applyRowFocusEffect(tv, row);

            // Crear el menú de clic derecho (estilo Discord)
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("✏️ Editar Juego");
            editItem.setOnAction(e -> abrirFormulario(row.getItem()));

            MenuItem deleteItem = new MenuItem("🗑️ Eliminar Juego");
            deleteItem.setOnAction(e -> {
                tvVideojuegos.getSelectionModel().select(row.getItem());
                onEliminar();
            });
            contextMenu.getItems().addAll(editItem, deleteItem);

            // Detectar los clics del ratón en la fila
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    // Doble clic izquierdo = Editar
                    if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2) {
                        abrirFormulario(row.getItem());
                    }
                    // Clic derecho = Mostrar Menú
                    else if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                }
            });

            return row;
        });

        // ... (Mantén tus mapeos de columnas intactos aquí) ...
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdVideojuego()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAño.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAño()));
        colNota.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNota()));

        // --- BARRAS DE PROGRESO DE COLORES PARA LA NOTA ---
        colNota.setCellFactory(col -> new TableCell<Videojuego, Integer>() {
            private final javafx.scene.control.ProgressBar pBar = new javafx.scene.control.ProgressBar();
            private final javafx.scene.control.Label lblNum = new javafx.scene.control.Label();
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(8, pBar, lblNum);

            {
                pBar.setPrefWidth(70);
                pBar.setPrefHeight(10);
                lblNum.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: 900;");
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Integer nota, boolean empty) {
                super.updateItem(nota, empty);
                if (empty || nota == null) {
                    setGraphic(null);
                } else {
                    pBar.setProgress(nota / 10.0); // La barra va de 0.0 a 1.0
                    lblNum.setText(nota.toString());

                    // Cambiamos el color de la barra según la nota
                    if (nota >= 9) {
                        pBar.setStyle("-fx-accent: #23a559; -fx-control-inner-background: #1e1f22; -fx-background-color: transparent;"); // Verde Obra Maestra
                        lblNum.setStyle("-fx-text-fill: #23a559; -fx-font-weight: 900; -fx-effect: dropshadow(gaussian, #23a559, 5, 0.3, 0, 0);");
                    } else if (nota >= 6) {
                        pBar.setStyle("-fx-accent: #fee75c; -fx-control-inner-background: #1e1f22; -fx-background-color: transparent;"); // Amarillo Jugable
                        lblNum.setStyle("-fx-text-fill: #fee75c; -fx-font-weight: 900;");
                    } else {
                        pBar.setStyle("-fx-accent: #da373c; -fx-control-inner-background: #1e1f22; -fx-background-color: transparent;"); // Rojo Malo
                        lblNum.setStyle("-fx-text-fill: #da373c; -fx-font-weight: 900;");
                    }
                    setGraphic(container);
                }
            }
        });        colGenero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenerosTexto() == null ? "-" : data.getValue().getGenerosTexto()));
        colPlataformas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlataformasTexto() == null ? "-" : data.getValue().getPlataformasTexto()));
        colFechaCompra.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaCompra() == null ? "-" : data.getValue().getFechaCompra().format(formatter)));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrecioCompra() == null ? "-" : data.getValue().getPrecioCompra() + " €"));

// --- COLUMNA PORTADA CON LUPA UNIFICADA ---
        colPortada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrlPortada()));
        colPortada.setCellFactory(col -> new TableCell<Videojuego, String>() {
            private final ImageView imageView = new ImageView();
            private final javafx.stage.Popup popup = new javafx.stage.Popup();
            private final ImageView popupImage = new ImageView();

            {
                // Configuración básica común
                this.setStyle("-fx-alignment: CENTER; -fx-cursor: hand;");
                imageView.setFitWidth(90);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                popupImage.setFitWidth(280); // Tamaño del holograma
                popupImage.setPreserveRatio(true);

                // --- LÓGICA DE LUPA UNIFICADA PARA LA CELDA COMPLETA ---

                // 1. Mostrar Holograma al entrar
                this.setOnMouseEntered(e -> {
                    if (this.getGraphic() != null && !this.isEmpty()) {
                        popup.getContent().clear(); // Limpiar rastro de hologramas viejos

                        Videojuego v = getTableView().getItems().get(getIndex());
                        Node contenidoHolograma;

                        // Decidir qué mostrar en el holograma gigante
                        if (v.getUrlPortada() == null || v.getUrlPortada().isBlank()) {
                            // Si no tiene foto, generamos avatar gigante
                            contenidoHolograma = com.micoleccion.utils.ModernUIUtils.generarAvatar(v.getTitulo(), 280, 380);
                        } else {
                            // Si sí tiene foto, usamos la popupImage con el marco de neón
                            popupImage.setImage(imageView.getImage());
                            javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane(popupImage);
                            wrapper.setStyle("-fx-padding: 5px;"); // Pequeño padding para que no toque el neón
                            contenidoHolograma = wrapper;
                        }

                        // El contenedor final con el neón común para ambos estilos
                        javafx.scene.layout.StackPane finalContainer = new javafx.scene.layout.StackPane(contenidoHolograma);
                        finalContainer.setStyle("-fx-background-color: #111214; -fx-border-color: #5865F2; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(88, 101, 242, 0.8), 25, 0.4, 0, 0);");

                        popup.getContent().add(finalContainer);
                        // Anclamos la popup a 'this' (la celda), así siempre hay padre
                        popup.show(this, e.getScreenX() + 25, e.getScreenY() + 25);
                    }
                });

                // 2. Seguir al ratón
                this.setOnMouseMoved(e -> {
                    if (popup.isShowing()) {
                        popup.setX(e.getScreenX() + 25);
                        popup.setY(e.getScreenY() + 25);
                    }
                });

                // 3. Ocultar Holograma al salir
                this.setOnMouseExited(e -> popup.hide());
            }

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Videojuego v = getTableView().getItems().get(getIndex());
                    Image img = (url == null || url.isBlank()) ? null : cargarImagen(url);

                    if (img == null || img.isError()) {
                        // Sigue sin foto: Mostramos Avatar Algorítmico en la tabla
                        setGraphic(com.micoleccion.utils.ModernUIUtils.generarAvatar(v.getTitulo(), 90, 120));
                    } else {
                        // Sí hay foto: Mostramos Imagen en la tabla
                        imageView.setImage(img);
                        setGraphic(imageView);
                    }
                }
            }
        });
        cargarCatalogos();

        // Diferir la carga inicial y aplicar animaciones a la ventana
        Platform.runLater(() -> {
            buscarYRefrescar();

            // --- BÚSQUEDA EN TIEMPO REAL (Live Search) ---
            txtBuscarTitulo.textProperty().addListener((obs, oldVal, newVal) -> buscarYRefrescar());
            cbGeneroFiltro.valueProperty().addListener((obs, oldVal, newVal) -> buscarYRefrescar());
            cbPlataformaFiltro.valueProperty().addListener((obs, oldVal, newVal) -> buscarYRefrescar());

            // --- ATAJOS DE TECLADO GLOBALES ---
            Scene scene = tvVideojuegos.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(event -> {
                    // CTRL + N = Nuevo Juego
                    if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.N) {
                        onNuevo();
                    }
                    // TECLA SUPRIMIR = Eliminar el juego seleccionado
                    else if (event.getCode() == javafx.scene.input.KeyCode.DELETE) {
                        if (!tvVideojuegos.getSelectionModel().isEmpty()) {
                            onEliminar();
                        }
                    }
                    // F5 = Recargar / Refrescar la tabla
                    else if (event.getCode() == javafx.scene.input.KeyCode.F5) {
                        buscarYRefrescar();
                    }
                });
            }

            // Animación de aparición de la ventana completa
            Parent root = scene.getRoot();
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
        String fallback = null;
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

            // --- ESTADÍSTICAS INTELIGENTES EN VIVO ---
            double totalDinero = lista.stream().filter(v -> v.getPrecioCompra() != null).mapToDouble(v -> v.getPrecioCompra().doubleValue()).sum();
            double notaMedia = lista.stream().filter(v -> v.getNota() != null).mapToDouble(Videojuego::getNota).average().orElse(0.0);

            String stats = String.format("🎮 %d Juegos   |   💰 Valor: %.2f €   |   ⭐ Nota Media: %.1f/10", lista.size(), totalDinero, notaMedia);
            lblEstado.setText(stats);

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

    @FXML
    private void onCerrar() {
        // Cierra la aplicación por completo
        System.exit(0);
    }
}