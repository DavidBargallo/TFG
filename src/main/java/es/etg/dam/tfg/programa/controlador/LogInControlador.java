package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LogInControlador {

    //Nombre usuario: David
    //Password: Contras3ña!

    private static final Logger logger = LoggerFactory.getLogger(LogInControlador.class);

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblError;

    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext applicationContext;

    @FXML
    public void initialize() {

    }

    @FXML
    public void iniciarSesion(ActionEvent event) {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            lblError.setText("Debes introducir usuario y contraseña.");
            lblError.setVisible(true);
            return;
        }

        try {
            Optional<Usuario> usuarioOptional = usuarioServicio.obtenerPorNombre(nombreUsuario);

            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                    lblError.setText("Inicio de sesión exitoso!");
                    lblError.setVisible(true);
                    Sesion.cerrarSesion();
                    Sesion.iniciarSesion(usuario);
                    abrirPantallaPrincipal(usuario, event);
                } else {
                    lblError.setText("Usuario o contraseña incorrectos.");
                    lblError.setVisible(true);
                }
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                lblError.setVisible(true);
            }
        } catch (Exception e) {
            logger.error("Error al iniciar sesión", e);
            lblError.setText("Error al iniciar sesión: " + e.getMessage());
            lblError.setVisible(true);
        }
    }

    @FXML
    public void abrirRegistro(ActionEvent event) {
        try {
            URL registroURL = getClass().getResource("/vista/pantalla_registro.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(registroURL);
            fxmlLoader.setControllerFactory(applicationContext::getBean); 
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.show();
        } catch (IOException e) {
            logger.error("Error al abrir la pantalla de registro", e);
            lblError.setText("No se pudo abrir la pantalla de registro.");
        }
    }

    private void abrirPantallaPrincipal(Usuario usuario, ActionEvent event) {
        try {
            URL bibliotecaURL = getClass().getResource("/vista/pantalla_biblioteca.fxml");
            FXMLLoader loader = new FXMLLoader(bibliotecaURL);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
    
            
            BibliotecaControlador controlador = loader.getController();
            controlador.inicializarBiblioteca();
    
            
            Stage oldStage = (Stage) ((Control) event.getSource()).getScene().getWindow();
            oldStage.close();
    
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Mi Biblioteca de Juegos");
            newStage.show();
    
        } catch (IOException e) {
            logger.error("Error al cargar la pantalla principal", e);
            lblError.setText("Error al cargar la pantalla principal: " + e.getMessage());
            lblError.setVisible(true);
        }
    }  
}