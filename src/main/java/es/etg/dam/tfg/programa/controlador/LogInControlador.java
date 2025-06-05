package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;
import es.etg.dam.tfg.programa.utils.FXMLSoporte;
import es.etg.dam.tfg.programa.utils.Mensajes;
import es.etg.dam.tfg.programa.utils.RutaFXML;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LogInControlador {

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
    public void iniciarSesion(ActionEvent event) {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            mostrarError(Mensajes.LOGIN_CAMPOS_VACIOS);
            return;
        }

        try {
            Optional<Usuario> usuarioOptional = usuarioServicio.obtenerPorNombre(nombreUsuario);

            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                    Sesion.cerrarSesion();
                    Sesion.iniciarSesion(usuario);
                    abrirPantallaPrincipal(event);
                } else {
                    mostrarError(Mensajes.LOGIN_CREDENCIALES_INVALIDAS);
                }
            } else {
                mostrarError(Mensajes.LOGIN_CREDENCIALES_INVALIDAS);
            }
        } catch (Exception e) {
            mostrarError(Mensajes.LOGIN_ERROR_GENERAL + e.getMessage());
        }
    }

    @FXML
    public void abrirRegistro(ActionEvent event) {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.REGISTRO, Mensajes.TITULO_REGISTRO, stage);
    }

    private void abrirPantallaPrincipal(ActionEvent event) {
        Stage stageActual = (Stage) ((Control) event.getSource()).getScene().getWindow();

        FXMLSoporte.abrirEInicializar(
                applicationContext,
                RutaFXML.BIBLIOTECA,
                Mensajes.TITULO_BIBLIOTECA,
                stageActual,
                (BibliotecaControlador controlador) -> controlador.inicializarBiblioteca()
        );
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}
