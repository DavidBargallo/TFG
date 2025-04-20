package es.etg.dam.tfg.programa.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoOrNombreUsuario(String correo, String nombreUsuario);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

}


