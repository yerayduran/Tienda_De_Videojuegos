/**
 * @author ManuelPerez
 * @version 1.0
 */

package com.micoleccion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // <--- Importante añadir este import

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/micoleccion/view/videojuegos-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        // <--- ESTA ES LA LÍNEA MÁGICA QUE QUITA LA BARRA BLANCA --->
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setTitle("CanalOcio IslaCristina");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}