package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.UsuarioVideojuegoRepositorio;
import es.etg.dam.tfg.programa.servicio.UsuarioVideojuegoServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioVideojuegoServicioTest {

    @Mock
    private UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;

    @InjectMocks
    private UsuarioVideojuegoServicio servicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerVideojuegosPorUsuario_devuelveLista() {
        System.out.println("Test: obtenerVideojuegosPorUsuario");

        List<UsuarioVideojuego> mockLista = List.of(new UsuarioVideojuego(), new UsuarioVideojuego());
        when(usuarioVideojuegoRepositorio.findWithVideojuegoCompletoByUsuarioId(1)).thenReturn(mockLista);

        List<UsuarioVideojuego> resultado = servicio.obtenerVideojuegosPorUsuario(1);
        assertEquals(2, resultado.size());

        System.out.println("Lista devuelta correctamente.");
    }

    @Test
    void tieneVideojuego_devuelveTrue() {
        System.out.println("Test: tieneVideojuego");

        when(usuarioVideojuegoRepositorio.existsByUsuarioIdAndVideojuegoId(1, 2)).thenReturn(true);

        boolean existe = servicio.tieneVideojuego(1, 2);
        assertTrue(existe);

        System.out.println("Relación encontrada correctamente.");
    }

    @Test
    void agregarVideojuegoAUsuario_guardaCorrectamente() {
        System.out.println("🔹 Test: agregarVideojuegoAUsuario");

        UsuarioVideojuego relacion = new UsuarioVideojuego();
        servicio.agregarVideojuegoAUsuario(relacion);

        verify(usuarioVideojuegoRepositorio).save(relacion);

        System.out.println("Relación guardada correctamente.");
    }

    @Test
    void obtenerVideojuegoPorId_devuelveOptional() {
        System.out.println("Test: obtenerVideojuegoPorId");

        UsuarioVideojuegoID id = new UsuarioVideojuegoID(1, 2);
        UsuarioVideojuego relacion = new UsuarioVideojuego();

        when(usuarioVideojuegoRepositorio.findById(id)).thenReturn(Optional.of(relacion));

        Optional<UsuarioVideojuego> resultado = servicio.obtenerVideojuegoPorId(id);
        assertTrue(resultado.isPresent());

        System.out.println("Relación encontrada correctamente.");
    }

    @Test
    void obtenerVideojuegosEnWishlist_devuelveListaFiltrada() {
        System.out.println("Test: obtenerVideojuegosEnWishlist");

        List<UsuarioVideojuego> mockLista = List.of(new UsuarioVideojuego());
        when(usuarioVideojuegoRepositorio.findConVideojuegoCompletoEnWishlist(1)).thenReturn(mockLista);

        List<UsuarioVideojuego> resultado = servicio.obtenerVideojuegosEnWishlist(1);
        assertEquals(1, resultado.size());

        System.out.println("Wishlist devuelta correctamente.");
    }

    @Test
    void eliminarRelacionUsuarioVideojuego_eliminaCorrectamente() {
        System.out.println("Test: eliminarRelacionUsuarioVideojuego");

        UsuarioVideojuegoID id = new UsuarioVideojuegoID(1, 2);
        servicio.eliminarRelacionUsuarioVideojuego(1, 2);

        verify(usuarioVideojuegoRepositorio).deleteById(id);

        System.out.println("Relación eliminada correctamente.");
    }
}
