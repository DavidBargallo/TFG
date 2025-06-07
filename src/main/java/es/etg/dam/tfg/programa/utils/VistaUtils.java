package es.etg.dam.tfg.programa.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VistaUtils {

    public static void mostrarSinResultados(VBox contenedor, Label mensajeLabel) {
        contenedor.getChildren().clear();
        mensajeLabel.setVisible(true);
        contenedor.getChildren().add(mensajeLabel);
    }
}
