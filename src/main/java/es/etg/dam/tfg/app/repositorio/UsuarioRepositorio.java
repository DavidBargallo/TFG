package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoOrNombreUsuario(String correo, String nombreUsuario);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

}


