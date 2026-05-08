/**
 * @author ManuelPerez
 * @version 1.0
 */

package com.micoleccion.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class ModernUIUtils {

    // Generador de portadas automáticas con iniciales y degradados únicos
    public static StackPane generarAvatar(String titulo, double width, double height) {
        String iniciales = "??";
        if (titulo != null && !titulo.trim().isEmpty()) {
            String[] palabras = titulo.trim().split(" ");
            if (palabras.length >= 2) {
                iniciales = (palabras[0].substring(0, 1) + palabras[1].substring(0, 1)).toUpperCase();
            } else if (palabras[0].length() >= 2) {
                iniciales = palabras[0].substring(0, 2).toUpperCase();
            } else {
                iniciales = palabras[0].toUpperCase();
            }
        }

        // Generar colores únicos basados en el nombre del juego (Magia matemática)
        int hash = Math.abs(titulo != null ? titulo.hashCode() : 0);
        Color color1 = Color.hsb(hash % 360, 0.7, 0.8);
        Color color2 = Color.hsb((hash + 50) % 360, 0.9, 0.6);

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(1, color2));

        Rectangle bg = new Rectangle(width, height);
        bg.setFill(gradient);
        bg.setArcWidth(12);
        bg.setArcHeight(12);

        Label lbl = new Label(iniciales);
        lbl.setStyle("-fx-font-size: " + (width / 2.5) + "px; -fx-text-fill: white; -fx-font-weight: 900;");

        StackPane pane = new StackPane(bg, lbl);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }
}