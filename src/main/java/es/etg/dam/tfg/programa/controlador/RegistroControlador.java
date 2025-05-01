package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

@Component
@RequiredArgsConstructor
public class RegistroControlador {

    private static final Logger logger = LoggerFactory.getLogger(RegistroControlador.class);

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private PasswordField txtConfirmarContrasena;

    @FXML
    private Label lblError;

    @Autowired
    private UsuarioServicio usuarioServicio;

    private final ApplicationContext applicationContext;


    @FXML
    public void initialize() {

    }

    @FXML
    public void registrarUsuario(ActionEvent event) {
        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass1 = txtContrasena.getText();
        String pass2 = txtConfirmarContrasena.getText();
    
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(pass1);
    
        try {
            usuarioServicio.registrarUsuario(nuevoUsuario, pass2);
            logger.info("Usuario registrado: {}", nombre);
            volverALogin(event);
        } catch (IllegalArgumentException e) {
            Platform.runLater(() -> lblError.setText(e.getMessage()));
            logger.error("Error al registrar usuario", e);
        } catch (Exception e) {
            Platform.runLater(() -> lblError.setText("Error al registrar usuario: " + e.getMessage()));
            logger.error("Error al registrar usuario", e);
        }
    }
    


    @FXML
    public void volverALogin(ActionEvent event) {
        try {
            URL loginURL = getClass().getResource("/vista/Pantalla_Inicio.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(loginURL);
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar sesión");
            stage.show();
        } catch (IOException e) {
            logger.error("Error al volver a la pantalla de inicio de sesión", e);
            lblError.setText("No se pudo volver a la pantalla de inicio de sesión.");
        }
    }
}
