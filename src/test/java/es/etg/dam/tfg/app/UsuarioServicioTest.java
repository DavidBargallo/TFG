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

class UsuarioServicioTest {

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

    //Registro exitoso
    @Test
    void registrarUsuario_valido_deberiaGuardarUsuario() {
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
    }

    //Contraseñas no coinciden
    @Test
    void registrarUsuario_contrasenasNoCoinciden_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("david@example.com");
        usuario.setContrasena("P@ssw0rd!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioServicio.registrarUsuario(usuario, "OtraClave123!"));

        assertTrue(ex.getMessage().contains("no coinciden"));
    }

    //Email no válido
    @Test
    void registrarUsuario_emailInvalido_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("malCorreo");
        usuario.setContrasena("P@ssw0rd!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioServicio.registrarUsuario(usuario, "P@ssw0rd!"));

        assertTrue(ex.getMessage().contains("correo electrónico"));
    }

    //El usuario ya existe
    @Test
    void registrarUsuario_usuarioYaExiste_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setCorreo("nuevo@example.com");
        usuario.setContrasena("P@ssw0rd!");

        when(usuarioRepositorio.existsByNombreUsuario("david")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioServicio.registrarUsuario(usuario, "P@ssw0rd!"));

        assertTrue(ex.getMessage().contains("usuario ya existe"));
    }

    //Login exitoso
    @Test
    void validarCredenciales_correctas_devuelveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setContrasena("hashedPassword");

        when(usuarioRepositorio.findByNombreUsuario("david")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("P@ssw0rd!", "hashedPassword")).thenReturn(true);

        Optional<Usuario> resultado = usuarioServicio.validarCredenciales("david", "P@ssw0rd!");
        assertTrue(resultado.isPresent());
    }

    // Login con contraseña incorrecta
    @Test
    void validarCredenciales_contrasenaIncorrecta_devuelveVacio() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("david");
        usuario.setContrasena("hashedPassword");

        when(usuarioRepositorio.findByNombreUsuario("david")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malaClave", "hashedPassword")).thenReturn(false);

        Optional<Usuario> resultado = usuarioServicio.validarCredenciales("david", "malaClave");
        assertTrue(resultado.isEmpty());
    }
}

