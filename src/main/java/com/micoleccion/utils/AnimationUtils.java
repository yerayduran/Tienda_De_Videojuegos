package com.micoleccion.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AnimationUtils {

    // Aparecer la ventana principal
    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setToValue(1.0);
        ft.play();
    }

    // Efecto de latido en los botones
    public static void applyBouncingEffect(Node node) {
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(150), node);
        stEnter.setToX(1.05); stEnter.setToY(1.05);
        ScaleTransition stExit = new ScaleTransition(Duration.millis(150), node);
        stExit.setToX(1.0); stExit.setToY(1.0);
        node.setOnMouseEntered(e -> { stExit.stop(); stEnter.playFromStart(); });
        node.setOnMouseExited(e -> { stEnter.stop(); stExit.playFromStart(); });
    }

    // Animación de fila (Desenfoque a los demás y pop-up agresivo)
    public static void applyRowFocusEffect(TableView<?> tv, TableRow<?> row) {
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(200), row);
        stEnter.setToX(1.03); stEnter.setToY(1.03);
        ScaleTransition stExit = new ScaleTransition(Duration.millis(200), row);
        stExit.setToX(1.0); stExit.setToY(1.0);
        TranslateTransition ttEnter = new TranslateTransition(Duration.millis(200), row);
        ttEnter.setToY(-6);
        TranslateTransition ttExit = new TranslateTransition(Duration.millis(200), row);
        ttExit.setToY(0);

        row.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (isHovered && !row.isEmpty()) {
                row.toFront();
                stExit.stop(); ttExit.stop();
                stEnter.playFromStart(); ttEnter.playFromStart();
                for (Node n : tv.lookupAll(".table-row-cell")) {
                    if (n != row && n instanceof TableRow && !((TableRow<?>)n).isEmpty()) {
                        GaussianBlur blur = new GaussianBlur(6);
                        ColorAdjust darken = new ColorAdjust();
                        darken.setBrightness(-0.5);
                        blur.setInput(darken);
                        n.setEffect(blur);
                    }
                }
            } else {
                stEnter.stop(); ttEnter.stop();
                stExit.playFromStart(); ttExit.playFromStart();
                for (Node n : tv.lookupAll(".table-row-cell")) n.setEffect(null);
            }
        });
    }

    // ABRIR UN MODAL INTERNO (FORMULARIO O ERROR)
    public static void showModal(StackPane overlay, Node content) {
        if (!overlay.isVisible()) {
            overlay.setVisible(true);
            overlay.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(250), overlay);
            ft.setToValue(1); ft.play();
        }
        // Si ya hay algo abierto (ej: el form) y abrimos un error encima, desenfocamos el form
        if (!overlay.getChildren().isEmpty()) {
            overlay.getChildren().get(overlay.getChildren().size()-1).setEffect(new GaussianBlur(10));
        }
        overlay.getChildren().add(content);
        content.setScaleX(0.6); content.setScaleY(0.6);
        ScaleTransition st = new ScaleTransition(Duration.millis(350), content);
        st.setToX(1); st.setToY(1);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT); // Animación tipo Bounce fluida
        st.play();
    }

    // CERRAR EL MODAL SUPERIOR
    public static void closeModal(StackPane overlay) {
        if (overlay.getChildren().isEmpty()) return;
        Node content = overlay.getChildren().get(overlay.getChildren().size() - 1);
        ScaleTransition st = new ScaleTransition(Duration.millis(200), content);
        st.setToX(0.8); st.setToY(0.8);
        FadeTransition ft = new FadeTransition(Duration.millis(200), content);
        ft.setToValue(0);
        st.setOnFinished(e -> {
            overlay.getChildren().remove(content);
            if (overlay.getChildren().isEmpty()) overlay.setVisible(false);
            else overlay.getChildren().get(overlay.getChildren().size() - 1).setEffect(null); // Quita el blur al form si cerramos el error
        });
        st.play(); ft.play();
    }
}