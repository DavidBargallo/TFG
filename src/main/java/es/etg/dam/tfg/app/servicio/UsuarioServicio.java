package es.etg.dam.tfg.app.servicio;

import es.etg.dam.tfg.app.modelo.Usuario;
import es.etg.dam.tfg.app.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    // Registrar nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    // Obtener usuario por ID
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
        return usuarioRepositorio.save(usuario); // save() también actualiza si el ID ya existe
    }

    public void eliminarUsuario(Integer id) {
        usuarioRepositorio.deleteById(id);
    }
}

