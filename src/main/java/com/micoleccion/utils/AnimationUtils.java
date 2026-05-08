package com.micoleccion.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class AnimationUtils {

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setToValue(1.0);
        ft.play();
    }

    public static void applyBouncingEffect(Node node) {
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(150), node);
        stEnter.setToX(1.05); stEnter.setToY(1.05);
        ScaleTransition stExit = new ScaleTransition(Duration.millis(150), node);
        stExit.setToX(1.0); stExit.setToY(1.0);
        node.setOnMouseEntered(e -> { stExit.stop(); stEnter.playFromStart(); });
        node.setOnMouseExited(e -> { stEnter.stop(); stExit.playFromStart(); });
    }

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

    public static void showModal(StackPane overlay, Node content) {
        if (!overlay.isVisible()) {
            overlay.setVisible(true);
            overlay.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(250), overlay);
            ft.setToValue(1); ft.play();
        }
        if (!overlay.getChildren().isEmpty()) overlay.getChildren().get(overlay.getChildren().size()-1).setEffect(new GaussianBlur(10));
        overlay.getChildren().add(content);
        content.setScaleX(0.6); content.setScaleY(0.6);
        ScaleTransition st = new ScaleTransition(Duration.millis(350), content);
        st.setToX(1); st.setToY(1);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }

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
            else overlay.getChildren().get(overlay.getChildren().size() - 1).setEffect(null);
        });
        st.play(); ft.play();
    }

    // --- NUEVO SISTEMA DE TOASTS (Mensajes que desaparecen solos) ---
    public static void showToast(StackPane overlay, String title, String message, String color) {
        boolean wasEmpty = overlay.getChildren().isEmpty();
        if (wasEmpty) {
            overlay.setVisible(true);
            overlay.setOpacity(0);
            FadeTransition ftIn = new FadeTransition(Duration.millis(250), overlay);
            ftIn.setToValue(1);
            ftIn.play();
        }

        VBox toast = new VBox(8);
        toast.setAlignment(Pos.CENTER);
        toast.setMaxWidth(380);
        toast.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        toast.setStyle("-fx-background-color: #111214; -fx-border-color: " + color + "; -fx-border-width: 2px; -fx-padding: 20; -fx-background-radius: 12; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, " + color + ", 15, 0.3, 0, 0);");
        toast.setMouseTransparent(true); // ¡Permite hacer clic a través del mensaje!

        Label lblT = new Label(title);
        lblT.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: 900; -fx-effect: dropshadow(gaussian, " + color + ", 8, 0.4, 0, 0);");

        Label lblM = new Label(message);
        lblM.setStyle("-fx-text-fill: #dbdee1; -fx-font-size: 14px;");
        lblM.setWrapText(true);
        lblM.setTextAlignment(TextAlignment.CENTER);

        toast.getChildren().addAll(lblT, lblM);

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 40, 0)); // Separación desde abajo

        overlay.getChildren().add(toast);

        // Animación de entrada (Sube desde abajo y aparece)
        TranslateTransition ttIn = new TranslateTransition(Duration.millis(400), toast);
        ttIn.setFromY(100); ttIn.setToY(0);
        ttIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        FadeTransition ftInToast = new FadeTransition(Duration.millis(400), toast);
        ftInToast.setFromValue(0); ftInToast.setToValue(1);

        javafx.animation.ParallelTransition ptIn = new javafx.animation.ParallelTransition(ttIn, ftInToast);

        // Pausa de 3.5 segundos para que lo lea
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(3.5));

        // Animación de salida (Baja y se desvanece)
        TranslateTransition ttOut = new TranslateTransition(Duration.millis(400), toast);
        ttOut.setFromY(0); ttOut.setToY(100);
        ttOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        FadeTransition ftOutToast = new FadeTransition(Duration.millis(400), toast);
        ftOutToast.setFromValue(1); ftOutToast.setToValue(0);

        javafx.animation.ParallelTransition ptOut = new javafx.animation.ParallelTransition(ttOut, ftOutToast);

        ptIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> ptOut.play());
        ptOut.setOnFinished(e -> {
            overlay.getChildren().remove(toast);
            // Si no hay nada más abierto (como el formulario), cerramos la capa oscura
            if (overlay.getChildren().isEmpty()) {
                FadeTransition ftOutOverlay = new FadeTransition(Duration.millis(250), overlay);
                ftOutOverlay.setToValue(0);
                ftOutOverlay.setOnFinished(event -> {
                    overlay.setVisible(false);
                    if (overlay.getParent() != null) {
                        Node mainContent = overlay.getParent().lookup("#mainContent");
                        if (mainContent != null) mainContent.setEffect(null);
                    }
                });
                ftOutOverlay.play();
            }
        });

        ptIn.play();
    }
}