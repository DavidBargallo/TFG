package es.etg.dam.tfg.programa.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import es.etg.dam.tfg.programa.utils.SpringFXMLLanzador;

@Component
@RequiredArgsConstructor
public class LogInControlador {

    private static final Logger logger = LoggerFactory.getLogger(LogInControlador.class);

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblError;

    private final UsuarioServicio usuarioServicio;
    private final SpringFXMLLanzador springFXMLLoader;

    @FXML
    public void iniciarSesion(ActionEvent event) {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            lblError.setText("Debes introducir usuario y contraseña.");
            return;
        }

        Usuario usuario = usuarioServicio.validarCredenciales(nombreUsuario, contrasena);
        if (usuario != null) {
            lblError.setText("");
            abrirPantallaPrincipal(usuario);
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }

    private void abrirPantallaPrincipal(Usuario usuario) {
        try {
            Parent root = springFXMLLoader.load(getClass().getResource("/es/etg/dam/tfg/app/vista/pantalla_biblioteca.fxml"));

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mi Biblioteca de Juegos");
            stage.show();

            // BibliotecaControlador controlador = springFXMLLoader.getController();
            // controlador.setUsuario(usuario);

        } catch (Exception e) {
            logger.error("Error al cargar la pantalla principal", e);
            lblError.setText("Error al cargar la pantalla principal.");
        }
    }
}

