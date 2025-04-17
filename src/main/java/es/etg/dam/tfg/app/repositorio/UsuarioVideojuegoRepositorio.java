package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.app.modelo.ids.UsuarioVideojuegoID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioVideojuegoRepositorio extends JpaRepository<UsuarioVideojuego, UsuarioVideojuegoID> {

    List<UsuarioVideojuego> findByUsuarioId(Integer usuarioId);

    List<UsuarioVideojuego> findByVideojuegoId(Integer videojuegoId);

    boolean existsByUsuarioIdAndVideojuegoId(Integer usuarioId, Integer videojuegoId);

    Optional<UsuarioVideojuego> findById(UsuarioVideojuegoID id);

    List<UsuarioVideojuego> findByUsuarioIdAndEnWishlist(Integer usuarioId, boolean enWishlist);
}

