package es.etg.dam.tfg.programa.utils;

import es.etg.dam.tfg.programa.modelo.Usuario;

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
}
