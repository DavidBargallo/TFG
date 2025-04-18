package es.etg.dam.tfg.app.controlador;

import es.etg.dam.tfg.app.servicio.UsuarioServicio;
import es.etg.dam.tfg.app.modelo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    @FXML
    public void iniciarSesion(ActionEvent event) {
        String nombreUsuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        Usuario usuario = usuarioServicio.validarCredenciales(nombreUsuario, contrasena);
        if (usuario != null) {
            abrirPantallaPrincipal(usuario);
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }

    private void abrirPantallaPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/etg/dam/tfg/app/vista/Biblioteca.fxml"));
            loader.setControllerFactory(param -> {
                return new Object();
            });

            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mi Biblioteca de Juegos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error al cargar la pantalla principal.");
        }
    }
}
