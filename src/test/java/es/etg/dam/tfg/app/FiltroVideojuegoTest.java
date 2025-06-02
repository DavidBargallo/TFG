package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.utils.FiltroVideojuego;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FiltroVideojuegoTest {

    private Videojuego crearJuego(String nombre, String consola, String genero, LocalDate fecha) {
        Videojuego v = new Videojuego();
        v.setNombre(nombre);
        v.setFechaLanzamiento(fecha);

        Consola c = new Consola();
        c.setNombre(consola);
        Genero g = new Genero();
        g.setNombre(genero);

        v.setConsolas(Set.of(c));
        v.setGeneros(Set.of(g));
        return v;
    }

    private final List<Videojuego> juegos = List.of(
            crearJuego("Final Fantasy VII", "PlayStation", "RPG", LocalDate.of(1997, 1, 31)),
            crearJuego("FIFA 22", "Xbox", "Deportes", LocalDate.of(2021, 10, 1)),
            crearJuego("Elden Ring", "PC", "RPG", LocalDate.of(2022, 2, 25)),
            crearJuego("Zelda", "Nintendo Switch", "Aventura", LocalDate.of(2017, 3, 3))
    );

    @Test
    void filtroPorNombre() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, "final", null, null, "Nombre A-Z");
        assertEquals(1, resultado.size());
        assertEquals("Final Fantasy VII", resultado.get(0).getNombre());
    }

    @Test
    void filtroPorConsola() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, null, "PC", null, "Nombre A-Z");
        assertEquals(1, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getNombre());
    }

    @Test
    void filtroPorGenero() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, null, null, "RPG", "Nombre A-Z");
        assertEquals(2, resultado.size());
    }

    @Test
    void filtroCompleto_nombreGeneroConsola() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, "elden", "PC", "RPG", "Nombre A-Z");
        assertEquals(1, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getNombre());
    }

    @Test
    void filtroPorNada_ordenPorNombre() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, "", null, null, "Nombre A-Z");
        assertEquals(4, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getNombre()); // Se debe mostrar en orden alfabético.
    }

    @Test
    void ordenPorFechaReciente() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, "", null, null, "Fecha más reciente");
        assertEquals("Elden Ring", resultado.get(0).getNombre()); // Se debe mostrar el más nuevo (por fecha de lanzamiento)
    }

    @Test
    void ordenPorFechaAntigua() {
        List<Videojuego> resultado = FiltroVideojuego.filtrarYOrdenar(juegos, "", null, null, "Fecha más antigua");
        assertEquals("Final Fantasy VII", resultado.get(0).getNombre()); // Se debe mostrar el más antiguo (por fecha de lanzamiento)
    }
}


