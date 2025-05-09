package es.etg.dam.tfg.programa.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.repositorio.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, String confirmarContrasena) { 
      
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty() ||
                usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty() ||
                usuario.getContrasena() == null || usuario.getContrasena().isEmpty() ||
                confirmarContrasena == null || confirmarContrasena.isEmpty()) { 
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        if (!EMAIL_PATTERN.matcher(usuario.getCorreo()).matches()) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }

        if (!PASSWORD_PATTERN.matcher(usuario.getContrasena()).matches()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
        }

        if (!usuario.getContrasena().equals(confirmarContrasena)) { 
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        if (usuarioRepositorio.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (usuarioRepositorio.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya existe");
        }

        String hashedContrasena = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedContrasena);
        return usuarioRepositorio.save(usuario);
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> obtenerPorNombre(String nombreUsuario) {
        return usuarioRepositorio.findByNombreUsuario(nombreUsuario);
    }

    public boolean existePorCorreoONombre(String correo, String nombreUsuario) {
        return usuarioRepositorio.existsByCorreoOrNombreUsuario(correo, nombreUsuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        usuarioRepositorio.deleteById(id);
    }

    public Optional<Usuario> validarCredenciales(String nombreUsuario, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
}
