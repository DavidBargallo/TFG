package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.repositorio.ConsolaRepositorio;
import es.etg.dam.tfg.programa.repositorio.GeneroRepositorio;
import es.etg.dam.tfg.programa.repositorio.VideojuegoRepositorio;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class VideojuegoServicioTest {

    @Mock
    private VideojuegoRepositorio videojuegoRepositorio;

    @Mock
    private GeneroRepositorio generoRepositorio;

    @Mock
    private ConsolaRepositorio consolaRepositorio;

    @InjectMocks
    private VideojuegoServicio videojuegoServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardar_conGenerosYConsolasExistentes_guardaCorrectamente() {
        System.out.println("🔹 Test: guardar_conGenerosYConsolasExistentes");

        Videojuego videojuego = new Videojuego();
        videojuego.setNombre("Test Game");
        videojuego.setFechaLanzamiento(LocalDate.now());

        Genero genero = new Genero();
        genero.setNombre("Acción");

        Consola consola = new Consola();
        consola.setNombre("PlayStation");

        videojuego.setGeneros(Set.of(genero));
        videojuego.setConsolas(Set.of(consola));

        when(generoRepositorio.findByNombre("Acción")).thenReturn(Optional.of(genero));
        when(consolaRepositorio.findByNombre("PlayStation")).thenReturn(Optional.of(consola));
        when(videojuegoRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(videojuegoRepositorio.findById(any())).thenReturn(Optional.of(videojuego));

        Videojuego guardado = videojuegoServicio.guardar(videojuego);

        assertNotNull(guardado);
        assertEquals("Test Game", guardado.getNombre());

        System.out.println("✅ Videojuego guardado correctamente.");
    }

    @Test
    void obtenerTodos_devuelveLista() {
        System.out.println("🔹 Test: obtenerTodos");

        when(videojuegoRepositorio.findAll()).thenReturn(List.of(new Videojuego(), new Videojuego()));
        List<Videojuego> lista = videojuegoServicio.obtenerTodos();
        assertEquals(2, lista.size());

        System.out.println("✅ Lista de videojuegos obtenida. Tamaño: " + lista.size());
    }

    @Test
    void obtenerPorId_devuelveVideojuego() {
        System.out.println("🔹 Test: obtenerPorId");

        Videojuego v = new Videojuego();
        v.setId(1);
        when(videojuegoRepositorio.findById(1)).thenReturn(Optional.of(v));
        Optional<Videojuego> result = videojuegoServicio.obtenerPorId(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());

        System.out.println("✅ Videojuego encontrado por ID.");
    }

    @Test
    void obtenerPorNombre_devuelveVideojuego() {
        System.out.println("🔹 Test: obtenerPorNombre");

        Videojuego v = new Videojuego();
        v.setNombre("FIFA 22");
        when(videojuegoRepositorio.findByNombre("FIFA 22")).thenReturn(Optional.of(v));
        Optional<Videojuego> result = videojuegoServicio.obtenerPorNombre("FIFA 22");
        assertTrue(result.isPresent());
        assertEquals("FIFA 22", result.get().getNombre());

        System.out.println("✅ Videojuego encontrado por nombre.");
    }

    @Test
    void obtenerPorNombreYFecha_devuelveCorrecto() {
        System.out.println("🔹 Test: obtenerPorNombreYFecha");

        LocalDate fecha = LocalDate.of(2022, 1, 1);
        Videojuego v = new Videojuego();
        v.setNombre("FIFA 22");
        v.setFechaLanzamiento(fecha);

        when(videojuegoRepositorio.findByNombreAndFechaLanzamiento("FIFA 22", fecha)).thenReturn(Optional.of(v));

        Optional<Videojuego> result = videojuegoServicio.obtenerPorNombreYFechaLanzamiento("FIFA 22", fecha);
        assertTrue(result.isPresent());
        assertEquals(fecha, result.get().getFechaLanzamiento());

        System.out.println("✅ Videojuego encontrado por nombre y fecha.");
    }

    @Test
    void buscarPorNombreConteniendo_devuelveListaFiltrada() {
        System.out.println("🔹 Test: buscarPorNombreConteniendo");

        when(videojuegoRepositorio.findByNombreContainingIgnoreCase("fifa")).thenReturn(List.of(new Videojuego()));
        List<Videojuego> lista = videojuegoServicio.buscarPorNombreConteniendo("fifa");
        assertEquals(1, lista.size());

        System.out.println("✅ Filtro por nombre parcial correcto.");
    }

    @Test
    void borrar_eliminaVideojuego() {
        System.out.println("🔹 Test: borrar");

        videojuegoServicio.borrar(123);
        verify(videojuegoRepositorio).deleteById(123);

        System.out.println("✅ Videojuego eliminado correctamente.");
    }
}



