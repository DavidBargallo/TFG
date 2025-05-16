package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import es.etg.dam.tfg.programa.utils.FXMLSoporte;
import es.etg.dam.tfg.programa.utils.RutaFXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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

    private final UsuarioServicio usuarioServicio;
    private final ApplicationContext applicationContext;

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
            mostrarError(e.getMessage());
            logger.error("Error de validación al registrar usuario", e);
        } catch (Exception e) {
            mostrarError("Error al registrar usuario: " + e.getMessage());
            logger.error("Error inesperado al registrar usuario", e);
        }
    }

    @FXML
    public void volverALogin(ActionEvent event) {
        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.LOGIN, "Iniciar sesión", stage);
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            lblError.setText(mensaje);
            lblError.setVisible(true);
        });
    }
}
