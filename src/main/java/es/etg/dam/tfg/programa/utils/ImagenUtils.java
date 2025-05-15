package es.etg.dam.tfg.programa.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagenUtils {
    public static void cargarImagen(ImageView imageView, String url) {
        try {
            imageView.setImage(new Image(url, true));
        } catch (Exception e) {
            imageView.setImage(new Image(ImagenUtils.class.getResourceAsStream("/placeholder.png")));
        }
    }
}

