package es.etg.dam.tfg.programa.utils;

import java.util.Set;
import java.util.stream.Collectors;

public class TextoUtils {

    public static <T> String obtenerNombres(Set<T> entidades) {
        return entidades.stream()
                .map(TextoUtils::obtenerNombreDeEntidad)
                .collect(Collectors.joining(", "));
    }

    private static String obtenerNombreDeEntidad(Object entidad) {
        try {
            return (String) entidad.getClass().getMethod("getNombre").invoke(entidad);
        } catch (Exception e) {
            return "";
        }
    }
}

