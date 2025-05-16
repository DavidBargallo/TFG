package es.etg.dam.tfg.programa.utils;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagenUtils {

    public static void cargarImagen(ImageView imageView, String url) {
        if (url == null || url.isBlank()) {
            cargarPlaceholder(imageView);
            return;
        }

        try {
            Image imagen = new Image(url, false);
            if (imagen.isError()) {
                System.err.println("Error al cargar imagen desde URL: " + url);
                cargarPlaceholder(imageView);
            } else {
                Platform.runLater(() -> imageView.setImage(imagen)); 
            }
        } catch (Exception e) {
            System.err.println("Excepción cargando imagen desde URL: " + url);
            e.printStackTrace();
            cargarPlaceholder(imageView);
        }
    }

    private static void cargarPlaceholder(ImageView imageView) {
        try {
            Image placeholder = new Image(ImagenUtils.class.getResourceAsStream("/fotos/placeholder.png"));
            Platform.runLater(() -> imageView.setImage(placeholder));
        } catch (Exception ex) {
            System.err.println("No se pudo cargar el placeholder.");
            ex.printStackTrace();
        }
    }
}




