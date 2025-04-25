package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioVideojuegoRepositorio extends JpaRepository<UsuarioVideojuego, UsuarioVideojuegoID> {

    List<UsuarioVideojuego> findByUsuarioId(Integer usuarioId);

    List<UsuarioVideojuego> findByVideojuegoId(Integer videojuegoId);

    boolean existsByUsuarioIdAndVideojuegoId(Integer usuarioId, Integer videojuegoId);

    Optional<UsuarioVideojuego> findById(UsuarioVideojuegoID id);

    List<UsuarioVideojuego> findByUsuarioIdAndEnWishlist(Integer usuarioId, boolean enWishlist);
}

