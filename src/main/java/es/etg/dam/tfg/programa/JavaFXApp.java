package es.etg.dam.tfg.programa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import javafx.application.Platform; 

import java.io.IOException;
import java.net.URL;

@Component
public class JavaFXApp extends Application {

    private ConfigurableApplicationContext applicationContext;
    private FXMLLoader loader;

    @Override
    public void init() throws Exception {
        applicationContext = App.getApplicationContext();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadFXML("/vista/Pantalla_Inicio.fxml");
        Parent root = loader.getRoot();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Iniciar sesión");
        primaryStage.show();
    }

    private void loadFXML(String Rutafxml) throws IOException {
        URL url = getClass().getResource(Rutafxml);
        if (url == null) {
            throw new IOException("No se pudo encontrar el recurso FXML: " + Rutafxml);
        }
        loader = new FXMLLoader(url);
        loader.setControllerFactory(applicationContext::getBean);
        loader.load();
    }

    @Override
    public void stop() throws Exception {
        if (applicationContext != null) {
            applicationContext.close();
        }
        Platform.exit();
    }
}