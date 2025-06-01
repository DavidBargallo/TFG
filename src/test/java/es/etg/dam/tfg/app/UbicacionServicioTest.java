package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.Ubicacion;
import es.etg.dam.tfg.programa.repositorio.UbicacionRepositorio;
import es.etg.dam.tfg.programa.servicio.UbicacionServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UbicacionServicioTest {

    @Mock
    private UbicacionRepositorio ubicacionRepositorio;

    @InjectMocks
    private UbicacionServicio ubicacionServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarSiNoExiste_noExiste_laGuarda() {
        System.out.println("Test: guardarSiNoExiste - no existe");

        when(ubicacionRepositorio.existsByLugarAndZonaAndIndicaciones("Estantería A", "Zona 1", "Junto al póster"))
            .thenReturn(false);

        Ubicacion mockUbicacion = new Ubicacion();
        mockUbicacion.setLugar("Estantería A");
        mockUbicacion.setZona("Zona 1");
        mockUbicacion.setIndicaciones("Junto al póster");

        when(ubicacionRepositorio.save(any())).thenReturn(mockUbicacion);

        Ubicacion resultado = ubicacionServicio.guardarSiNoExiste("Estantería A", "Zona 1", "Junto al póster");

        assertNotNull(resultado);
        assertEquals("Estantería A", resultado.getLugar());
        verify(ubicacionRepositorio).save(any());

        System.out.println("Ubicación guardada correctamente.");
    }

    @Test
    void guardarSiNoExiste_existe_laDevuelve() {
        System.out.println("Test: guardarSiNoExiste - ya existe");

        Ubicacion existente = new Ubicacion();
        existente.setLugar("Armario");
        existente.setZona("Zona 2");
        existente.setIndicaciones("Abajo del todo");

        when(ubicacionRepositorio.existsByLugarAndZonaAndIndicaciones("Armario", "Zona 2", "Abajo del todo"))
            .thenReturn(true);

        when(ubicacionRepositorio.findAll()).thenReturn(List.of(existente));

        Ubicacion resultado = ubicacionServicio.guardarSiNoExiste("Armario", "Zona 2", "Abajo del todo");

        assertNotNull(resultado);
        assertEquals("Armario", resultado.getLugar());
        verify(ubicacionRepositorio, never()).save(any());

        System.out.println("Ubicación existente devuelta correctamente.");
    }
}

