package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import es.etg.dam.tfg.programa.utils.SpringFXMLLanzador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
@Controller
public class RegistroControlador {

    private static final Logger logger = LoggerFactory.getLogger(RegistroControlador.class);

    private final UsuarioServicio usuarioServicio;
    private final SpringFXMLLanzador springFXMLLanzador;

    @FXML
    private TextField txtNuevoUsuario;

    @FXML
    private PasswordField txtNuevaContrasena;

    @FXML
    private PasswordField txtRepetirContrasena;

    @FXML
    private Label lblError;

    @FXML
    public void registrarUsuario(ActionEvent event) {
        String nombre = txtNuevoUsuario.getText().trim();
        String pass1 = txtNuevaContrasena.getText();
        String pass2 = txtRepetirContrasena.getText();

        if (nombre.isBlank() || pass1.isBlank() || pass2.isBlank()) {
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
        nuevoUsuario.setContrasena(pass1);

        usuarioServicio.registrarUsuario(nuevoUsuario);
        logger.info("Usuario registrado: {}", nombre);
        volverAlLogin(event);
    }

    @FXML
    public void volverAlLogin(ActionEvent event) {
        try {
            Parent root = springFXMLLanzador.load(getClass().getResource("/es/etg/dam/tfg/pograma/app/vista/pantalla_inicio.fxml"));
            Stage stage = (Stage) txtNuevoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();
        } catch (Exception e) {
            logger.error("Error al volver al login", e);
            lblError.setText("No se pudo volver al login.");
        }
    }
}

