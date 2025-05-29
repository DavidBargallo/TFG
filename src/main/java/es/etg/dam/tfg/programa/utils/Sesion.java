package es.etg.dam.tfg.programa.utils;

import es.etg.dam.tfg.programa.modelo.Usuario;
import javafx.scene.control.Menu;

public class Sesion {
    private static Usuario usuarioActual;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public static void nombreUsuario(Menu menuCuenta){
        if (Sesion.haySesionActiva()) {
            menuCuenta.setText(Sesion.getUsuarioActual().getNombreUsuario());
        } else {
            FXMLSoporte.mostrarError(Mensajes.USUARIO_NO_LOGUEADO);
        }
    }
}
