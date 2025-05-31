package es.etg.dam.tfg.app;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.repositorio.UsuarioRepositorio;
import es.etg.dam.tfg.programa.servicio.UsuarioServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServicio usuarioServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarUsuario_valido_deberiaGuardarUsuario() {
        System.out.println("🔹 Test: registrarUsuario_valido_deberiaGuardarUsuario");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("david@example.com");
        usuario.setContrasena("P@ssw0rd!");

        when(usuarioRepositorio.existsByNombreUsuario("david")).thenReturn(false);
        when(usuarioRepositorio.existsByCorreo("david@example.com")).thenReturn(false);
        when(passwordEncoder.encode("P@ssw0rd!")).thenReturn("hashed");
        when(usuarioRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = usuarioServicio.registrarUsuario(usuario, "P@ssw0rd!");
        assertEquals("hashed", result.getContrasena());

        System.out.println("✅ Registro exitoso confirmado.");
    }

    @Test
    void registrarUsuario_contrasenasNoCoinciden_lanzaExcepcion() {
        System.out.println("🔹 Test: registrarUsuario_contrasenasNoCoinciden_lanzaExcepcion");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("david@example.com");
        usuario.setContrasena("P@ssw0rd!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioServicio.registrarUsuario(usuario, "OtraClave123!"));

        System.out.println("✅ Excepción lanzada: " + ex.getMessage());
    }

    @Test
    void registrarUsuario_emailInvalido_lanzaExcepcion() {
        System.out.println("🔹 Test: registrarUsuario_emailInvalido_lanzaExcepcion");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("malCorreo");
        usuario.setContrasena("P@ssw0rd!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioServicio.registrarUsuario(usuario, "P@ssw0rd!"));

        System.out.println("✅ Excepción lanzada: " + ex.getMessage());
    }

    @Test
    void registrarUsuario_usuarioYaExiste_lanzaExcepcion() {
        System.out.println("🔹 Test: registrarUsuario_usuarioYaExiste_lanzaExcepcion");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("nuevo@example.com");
        usuario.setContrasena("P@ssw0rd!");

        when(usuarioRepositorio.existsByNombreUsuario("david")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioServicio.registrarUsuario(usuario, "P@ssw0rd!"));

        System.out.println("✅ Excepción lanzada: " + ex.getMessage());
    }

    @Test
    void validarCredenciales_correctas_devuelveUsuario() {
        System.out.println("🔹 Test: validarCredenciales_correctas_devuelveUsuario");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setContrasena("hashedPassword");

        when(usuarioRepositorio.findByNombreUsuario("david")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("P@ssw0rd!", "hashedPassword")).thenReturn(true);

        Optional<Usuario> resultado = usuarioServicio.validarCredenciales("david", "P@ssw0rd!");
        assertTrue(resultado.isPresent());

        System.out.println("✅ Login correcto con credenciales válidas.");
    }

    @Test
    void validarCredenciales_contrasenaIncorrecta_devuelveVacio() {
        System.out.println("🔹 Test: validarCredenciales_contrasenaIncorrecta_devuelveVacio");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setContrasena("hashedPassword");

        when(usuarioRepositorio.findByNombreUsuario("david")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malaClave", "hashedPassword")).thenReturn(false);

        Optional<Usuario> resultado = usuarioServicio.validarCredenciales("david", "malaClave");
        assertTrue(resultado.isEmpty());

        System.out.println("✅ Login fallido correctamente detectado.");
    }
}

