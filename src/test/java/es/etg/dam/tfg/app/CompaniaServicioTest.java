package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.Compania;
import es.etg.dam.tfg.programa.repositorio.CompaniaRepositorio;
import es.etg.dam.tfg.programa.servicio.CompaniaServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompaniaServicioTest {

    @Mock
    private CompaniaRepositorio companiaRepositorio;

    @InjectMocks
    private CompaniaServicio companiaServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarSiNoExiste_companiaNoExiste_laCrea() {
        System.out.println("Test: guardarSiNoExiste - crea nueva");

        when(companiaRepositorio.findByNombreAndPais("Sony", "Japón")).thenReturn(Optional.empty());

        Compania companiaMock = new Compania();
        companiaMock.setNombre("Sony");
        companiaMock.setPais("Japón");

        when(companiaRepositorio.saveAndFlush(any())).thenReturn(companiaMock);

        Compania resultado = companiaServicio.guardarSiNoExiste("Sony", "Japón", 1946, "sony.com");

        assertNotNull(resultado);
        assertEquals("Sony", resultado.getNombre());
        verify(companiaRepositorio).saveAndFlush(any());

        System.out.println("Compañía creada correctamente.");
    }

    @Test
    void guardarSiNoExiste_companiaExiste_laDevuelve() {
        System.out.println("Test: guardarSiNoExiste - ya existe");

        Compania existente = new Compania();
        existente.setNombre("Nintendo");
        existente.setPais("Japón");

        when(companiaRepositorio.findByNombreAndPais("Nintendo", "Japón")).thenReturn(Optional.of(existente));

        Compania resultado = companiaServicio.guardarSiNoExiste("Nintendo", "Japón", 1889, "nintendo.com");

        assertNotNull(resultado);
        assertEquals("Nintendo", resultado.getNombre());
        verify(companiaRepositorio, never()).saveAndFlush(any());

        System.out.println("Compañía existente devuelta correctamente.");
    }

    @Test
    void guardarSiNoExiste_nombreNulo_noGuarda() {
        System.out.println("Test: guardarSiNoExiste - nombre nulo");

        Compania resultado = companiaServicio.guardarSiNoExiste(null, "España", 2000, "web.com");

        assertNull(resultado);
        verify(companiaRepositorio, never()).saveAndFlush(any());

        System.out.println("No se guardó por nombre inválido.");
    }
}

