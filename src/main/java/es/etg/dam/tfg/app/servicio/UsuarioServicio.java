package es.etg.dam.tfg.app.servicio;

import es.etg.dam.tfg.app.modelo.Usuario;
import es.etg.dam.tfg.app.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) {
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

    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    public void eliminarUsuario(Integer id) {
        usuarioRepositorio.deleteById(id);
    }

    public Usuario validarCredenciales(String nombreUsuario, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
    
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                return usuario;
            }
        }
    
        return null;
    }
}

