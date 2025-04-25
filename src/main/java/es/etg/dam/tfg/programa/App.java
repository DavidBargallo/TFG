package es.etg.dam.tfg.programa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

    @Override
    public void init() throws Exception {
        springContext = new org.springframework.boot.builder.SpringApplicationBuilder(App.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Pantalla_Inicio.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Iniciar sesión");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }
}