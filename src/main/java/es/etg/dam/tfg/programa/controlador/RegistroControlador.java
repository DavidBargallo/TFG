package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class RegistroControlador {

    private static final Logger logger = LoggerFactory.getLogger(RegistroControlador.class);

    private final UsuarioServicio usuarioServicio;
    private final ApplicationContext applicationContext;

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

    @FXML
    @Transactional
    public void registrarUsuario(ActionEvent event) {
        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass1 = txtContrasena.getText();
        String pass2 = txtConfirmarContrasena.getText();

        if (nombre.isBlank() || correo.isBlank() || pass1.isBlank() || pass2.isBlank()) {
            lblError.setText("Todos los campos son obligatorios.");
            return;
        }

        if (!pass1.equals(pass2)) {
            lblError.setText("Las contraseñas no coinciden.");
            return;
        }

        if (usuarioServicio.obtenerPorNombre(nombre).isPresent()) {
            lblError.setText("El nombre de usuario ya está en uso.");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(pass1);

        try {
            usuarioServicio.registrarUsuario(nuevoUsuario);
            logger.info("Usuario registrado: {}", nombre);
            volverALogin(event);
        } catch (Exception e) {
            logger.error("Error al registrar usuario", e);
            lblError.setText("Error al registrar usuario: " + e.getMessage());
        }
    }

    @FXML
    public void volverALogin(ActionEvent event) {
        try {
            URL url = getClass().getResource("/vista/Pantalla_Inicio.fxml");
            if (url == null) {
                lblError.setText("No se pudo encontrar el recurso FXML: /vista/Pantalla_Inicio.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();
        } catch (IOException e) {
            logger.error("Error al volver al login", e);
            lblError.setText("No se pudo volver al login: " + e.getMessage()); 
        }
    }
}