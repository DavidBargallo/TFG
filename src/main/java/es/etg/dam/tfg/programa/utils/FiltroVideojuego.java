package es.etg.dam.tfg.programa.utils;

import es.etg.dam.tfg.programa.modelo.Videojuego;


import java.util.Comparator;
import java.util.List;


public class FiltroVideojuego {

    public static List<Videojuego> filtrarYOrdenar(List<Videojuego> juegos, String nombre, String nombreConsola,
            String genero, String orden) {
        return juegos.stream()
                .filter(j -> nombre == null || nombre.isBlank() || j.getNombre().toLowerCase().contains(nombre))
                .filter(j -> nombreConsola == null || nombreConsola.equals("Todas") ||
                        j.getConsolas().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombreConsola)))
                .filter(j -> genero == null || genero.equals("Todos") ||
                        j.getGeneros().stream().anyMatch(g -> g.getNombre().equalsIgnoreCase(genero)))
                .sorted(obtenerComparador(orden))
                .toList();
    }

    private static Comparator<Videojuego> obtenerComparador(String orden) {
        return switch (orden) {
            case "Nombre A-Z" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Nombre Z-A" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
            case "Fecha más reciente" -> Comparator.comparing(Videojuego::getFechaLanzamiento).reversed();
            case "Fecha más antigua" -> Comparator.comparing(Videojuego::getFechaLanzamiento);
            default -> Comparator.comparing(Videojuego::getNombre);
        };
    }
}
