package es.etg.dam.tfg.programa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import es.etg.dam.tfg.programa.utils.ArranqueSpringBoot;

public class App extends Application {

    private ApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(ArranqueSpringBoot.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/etg/dam/tfg/programa/vista/Pantalla_Inicio.fxml"));

        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Iniciar sesión");
        stage.show();
    }
}
