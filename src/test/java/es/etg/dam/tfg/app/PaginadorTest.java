package es.etg.dam.tfg.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.etg.dam.tfg.programa.utils.Paginador;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaginadorTest {

    private Paginador<String> paginador;

    @BeforeEach
    void setUp() {
        List<String> datos = List.of(
                "Juego1", "Juego2", "Juego3", "Juego4",
                "Juego5", "Juego6", "Juego7", "Juego8",
                "Juego9", "Juego10", "Juego11", "Juego12"
        );
        paginador = new Paginador<>(datos, 5);
    }

    @Test
    void getPaginaActual_devuelveCorrectosElementos() {
        List<String> pagina = paginador.getPaginaActual();
        assertEquals(5, pagina.size());
        assertEquals("Juego1", pagina.get(0));
        assertEquals("Juego5", pagina.get(4));
    }

    @Test
    void irSiguiente_navegaCorrectamente() {
        paginador.irSiguiente();
        List<String> pagina = paginador.getPaginaActual();
        assertEquals("Juego6", pagina.get(0));
        assertEquals("Juego10", pagina.get(4));
    }

    @Test
    void irAnterior_navegaCorrectamente() {
        paginador.irSiguiente();
        paginador.irAnterior();
        List<String> pagina = paginador.getPaginaActual();
        assertEquals("Juego1", pagina.get(0));
    }

    @Test
    void getTotalPaginas_devuelveCorrecto() {
        assertEquals(3, paginador.getTotalPaginas());
    }

    @Test
    void getPaginaActualNumero_iniciaEnUno() {
        assertEquals(1, paginador.getPaginaActualNumero());
        paginador.irSiguiente();
        assertEquals(2, paginador.getPaginaActualNumero());
    }

    @Test
    void puedeIrSiguiente_y_puedeIrAnterior() {
        assertTrue(paginador.puedeIrSiguiente());
        assertFalse(paginador.puedeIrAnterior());

        paginador.irSiguiente();
        assertTrue(paginador.puedeIrAnterior());
    }

    @Test
    void reset_reseteaALaPrimeraPagina() {
        paginador.irSiguiente();
        paginador.irSiguiente();
        paginador.reset();
        assertEquals("Juego1", paginador.getPaginaActual().get(0));
    }

    @Test
    void estaVacio_detectaListaVacia() {
        Paginador<String> vacio = new Paginador<>(List.of(), 5);
        assertTrue(vacio.estaVacio());
        assertEquals(0, vacio.getTotalPaginas());
    }

    @Test
    void ultimaPagina_tieneElementosRestantes() {
        paginador.irSiguiente(); // Página 2
        paginador.irSiguiente(); // Página 3
        List<String> ultimaPagina = paginador.getPaginaActual();
        assertEquals(2, ultimaPagina.size()); // Solo Juego11 y Juego12
    }
}

