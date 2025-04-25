package es.etg.dam.tfg.programa.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.repositorio.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder; 

    @Transactional 
    public Usuario registrarUsuario(Usuario usuario) {
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