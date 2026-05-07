/**
 * @author ManuelPerez
 * @version 1.0
 */

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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class VideojuegoController {

    @FXML private TextField txtBuscarTitulo;
    @FXML private ComboBox<Genero> cbGeneroFiltro;
    @FXML private ComboBox<Plataforma> cbPlataformaFiltro;
    @FXML private TableView<Videojuego> tvVideojuegos;
    @FXML private TableColumn<Videojuego, Integer> colId;
    @FXML private TableColumn<Videojuego, String> colTitulo;
    @FXML private TableColumn<Videojuego, Integer> colAño;
    @FXML private TableColumn<Videojuego, Integer> colNota;
    @FXML private TableColumn<Videojuego, String> colGenero;
    @FXML private TableColumn<Videojuego, String> colPlataformas;
    @FXML private TableColumn<Videojuego, String> colFechaCompra;
    @FXML private TableColumn<Videojuego, String> colPrecio;
    @FXML private Label lblEstado;

    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOMySQL();
    private final GeneroDAO generoDAO = new GeneroDAOMySQL();
    private final PlataformaDAO plataformaDAO = new PlataformaDAOMySQL();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        tvVideojuegos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (!tvVideojuegos.getStyleClass().contains("tabla-videojuegos")) {
            tvVideojuegos.getStyleClass().add("tabla-videojuegos");
        }

        if (lblEstado != null && !lblEstado.getStyleClass().contains("lbl-estado")) {
            lblEstado.getStyleClass().add("lbl-estado");
        }

        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdVideojuego()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAño.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAño()));
        colNota.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNota()));
        colGenero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreGenero()));
        colPlataformas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlataformasTexto()));
        colFechaCompra.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaCompra() == null ? "" : data.getValue().getFechaCompra().format(formatter)
        ));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPrecioCompra() == null ? "" : data.getValue().getPrecioCompra() + " EUR"
        ));

        cargarCssPrincipal();
        cargarCombos();
        buscarYRefrescar();
    }

    private void cargarCssPrincipal() {
        try {
            if (tvVideojuegos.getScene() != null) {
                String cssUrl = MainApp.class
                        .getResource("/com/micoleccion/css/VideojuegoController.css")
                        .toExternalForm();

                if (!tvVideojuegos.getScene().getStylesheets().contains(cssUrl)) {
                    tvVideojuegos.getScene().getStylesheets().add(cssUrl);
                }

                Parent root = tvVideojuegos.getScene().getRoot();
                if (root != null && !root.getStyleClass().contains("root-pane")) {
                    root.getStyleClass().add("root-pane");
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar videojuego-main.css");
        }
    }

    @FXML
    private void onBuscar() {
        buscarYRefrescar();
    }

    @FXML
    private void onLimpiarFiltros() {
        txtBuscarTitulo.clear();
        cbGeneroFiltro.getSelectionModel().clearSelection();
        cbPlataformaFiltro.getSelectionModel().clearSelection();
        buscarYRefrescar();
    }

    @FXML
    private void onRecargar() {
        cargarCombos();
        buscarYRefrescar();
    }

    @FXML
    private void onNuevo() {
        abrirDialogo(null);
    }

    @FXML
    private void onEditar() {
        Videojuego seleccionado = tvVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarInfo("Selecciona un videojuego para editar.");
            return;
        }
        abrirDialogo(seleccionado);
    }

    @FXML
    private void onEliminar() {
        Videojuego seleccionado = tvVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarInfo("Selecciona un videojuego para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminacion");
        confirm.setHeaderText("Se va a eliminar: " + seleccionado.getTitulo());
        confirm.setContentText("Esta accion no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                videojuegoDAO.eliminar(seleccionado.getIdVideojuego());
                buscarYRefrescar();
            } catch (SQLException e) {
                mostrarError("No se pudo eliminar el videojuego.", e);
            }
        }
    }

    private void abrirDialogo(Videojuego videojuego) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/micoleccion/view/videojuego-form-view.fxml")
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);

            String cssForm = MainApp.class
                    .getResource("/com/micoleccion/css/VideojuegoFormController.css")
                    .toExternalForm();
            scene.getStylesheets().add(cssForm);

            if (!root.getStyleClass().contains("root-pane")) {
                root.getStyleClass().add("root-pane");
            }

            VideojuegoFormController controller = loader.getController();
            controller.setDatos(videojuego, generoDAO.listarTodos(), plataformaDAO.listarTodas());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(videojuego == null ? "Nuevo videojuego" : "Editar videojuego");
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isGuardado()) {
                buscarYRefrescar();
            }
        } catch (Exception e) {
            mostrarError("No se pudo abrir el formulario.", e);
        }
    }

    private void cargarCombos() {
        try {
            List<Genero> generos = generoDAO.listarTodos();
            List<Plataforma> plataformas = plataformaDAO.listarTodas();

            cbGeneroFiltro.setItems(FXCollections.observableArrayList(generos));
            cbPlataformaFiltro.setItems(FXCollections.observableArrayList(plataformas));
        } catch (SQLException e) {
            mostrarError("Error cargando catalogos.", e);
        }
    }

    private void buscarYRefrescar() {
        try {
            String titulo = txtBuscarTitulo.getText();
            Genero genero = cbGeneroFiltro.getValue();
            Plataforma plataforma = cbPlataformaFiltro.getValue();

            List<Videojuego> lista = videojuegoDAO.buscar(
                    titulo,
                    genero == null ? null : genero.idGenero(),
                    plataforma == null ? null : plataforma.idPlataforma()
            );

            tvVideojuegos.setItems(FXCollections.observableArrayList(lista));
            lblEstado.setText(lista.size() + " videojuegos cargados");
        } catch (SQLException e) {
            mostrarError("Error ejecutando la busqueda.", e);
        }
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String msg, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
