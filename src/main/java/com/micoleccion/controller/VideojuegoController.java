package com.micoleccion.controller;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

        // Mapeo de columnas con el modelo Videojuego
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdVideojuego()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAño.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAño()));
        colNota.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNota()));

        colGenero.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getGenerosTexto() == null ? "-" : data.getValue().getGenerosTexto()
        ));
        colPlataformas.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPlataformasTexto() == null ? "-" : data.getValue().getPlataformasTexto()
        ));

        colFechaCompra.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaCompra() == null ? "-" : data.getValue().getFechaCompra().format(formatter)
        ));

        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPrecioCompra() == null ? "-" : data.getValue().getPrecioCompra() + " €"
        ));

        colPortada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrlPortada()));
        colPortada.setCellFactory(col -> new TableCell<Videojuego, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(45);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                setGraphic(imageView);
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

        // Diferir la carga inicial de datos hasta que la Scene esté lista
        Platform.runLater(this::buscarYRefrescar);
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
        cbGeneroFiltro.setValue(null);
        cbPlataformaFiltro.setValue(null);
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
            mostrarError("Error en la búsqueda", e);
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
            mostrarInfoPersonalizada("⚠ AVISO", "Selecciona un juego para editar.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    private void abrirFormulario(Videojuego v) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/micoleccion/view/videojuego-form-view.fxml"));
            Parent root = loader.load();

            VideojuegoFormController controller = loader.getController();
            try {
                controller.setDatos(v, generoDAO.listarTodos(), plataformaDAO.listarTodas());
            } catch (SQLException e) {
                mostrarError("Error al cargar datos", e);
                return;
            }

            Stage stage = new Stage();
            stage.setTitle(v == null ? "Añadir Juego" : "Editar Juego");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);

            // Cargar CSS de forma robusta
            try {
                String css = getClass().getResource("/com/micoleccion/css/VideojuegoFormController.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (NullPointerException e) {
                System.err.println("Advertencia: CSS no encontrado en: /com/micoleccion/css/VideojuegoFormController.css");
            }

            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isGuardado()) {
                buscarYRefrescar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el formulario", e);
        }
    }

    @FXML
    private void onEliminar() {
        Videojuego seleccionado = tvVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarInfoPersonalizada("⚠ AVISO", "Selecciona un juego para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar " + seleccionado.getTitulo() + "?", ButtonType.YES, ButtonType.NO);
        alert.initOwner(tvVideojuegos.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Eliminar videojuego");
        alert.getDialogPane().getStyleClass().add("custom-alert");

        Button btnYes = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        Button btnNo = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        btnYes.getStyleClass().add("dialog-btn-danger");
        btnNo.getStyleClass().add("dialog-btn-secondary");

        // Cargar CSS para la alerta
        try {
            String css = getClass().getResource("/com/micoleccion/css/VideojuegoController.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Advertencia: CSS no encontrado para alerta de eliminación");
        }

        alert.showAndWait().ifPresent(tipo -> {
            if (tipo == ButtonType.YES) {
                try {
                    videojuegoDAO.eliminar(seleccionado.getIdVideojuego());
                    buscarYRefrescar();
                } catch (SQLException e) {
                    mostrarError("Error al eliminar", e);
                }
            }
        });
    }

    @FXML
    private void onRecargar() {
        buscarYRefrescar();
    }

    private void mostrarInfoPersonalizada(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(tvVideojuegos.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(msg);
        alert.getDialogPane().getStyleClass().add("custom-alert");

        // Cargar CSS para la alerta
        try {
            String css = getClass().getResource("/com/micoleccion/css/VideojuegoController.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Advertencia: CSS no encontrado para alerta de información");
        }

        Button btnOk = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) {
            btnOk.getStyleClass().add("dialog-btn-danger");
        }

        alert.showAndWait();
    }

    private void mostrarError(String msg, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(tvVideojuegos.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        alert.setContentText(e.getMessage() != null ? e.getMessage() : e.toString());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        // Cargar CSS para la alerta
        try {
            String css = getClass().getResource("/com/micoleccion/css/VideojuegoController.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (NullPointerException e2) {
            System.err.println("Advertencia: CSS no encontrado para alerta de error");
        }

        Button btnOk = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) {
            btnOk.getStyleClass().add("dialog-btn-danger");
        }

        alert.showAndWait();
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        Stage stage = (Stage) tvVideojuegos.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void onMinimizar() {
        Stage stage = (Stage) tvVideojuegos.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void onCerrar() {
        Stage stage = (Stage) tvVideojuegos.getScene().getWindow();
        stage.close();
    }
}