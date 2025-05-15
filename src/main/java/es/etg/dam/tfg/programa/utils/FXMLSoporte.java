package es.etg.dam.tfg.programa.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class FXMLSoporte {

    public static void abrirVentana(ApplicationContext context, String rutaFXML, String titulo, Stage owner) {
    try {
        URL url = FXMLSoporte.class.getResource(rutaFXML);
        if (url == null) {
            mostrarError("No se pudo encontrar el archivo FXML: " + rutaFXML);
            return;
        }

        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.show();

        if (owner != null) {
            owner.close();
        }

    } catch (IOException e) {
        mostrarError("No se pudo abrir la ventana: " + e.getMessage());
    }
}


    public static <T> void abrirEInicializar(ApplicationContext context, String rutaFXML, String titulo, Stage owner, Consumer<T> inicializador) {
    try {
        URL url = FXMLSoporte.class.getResource(rutaFXML);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();

        T controller = loader.getController();
        inicializador.accept(controller);

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        if (owner != null) stage.initOwner(owner);
        stage.show();

    } catch (IOException e) {
        mostrarError("No se pudo abrir la pantalla: " + e.getMessage());
    }
}


    public static void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

