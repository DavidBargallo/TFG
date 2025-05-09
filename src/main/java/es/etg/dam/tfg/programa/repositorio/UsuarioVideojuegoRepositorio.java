package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioVideojuegoRepositorio extends JpaRepository<UsuarioVideojuego, UsuarioVideojuegoID> {

    @Query("SELECT uv FROM UsuarioVideojuego uv " +
       "JOIN FETCH uv.videojuego v " +
       "LEFT JOIN FETCH v.generos " +
       "LEFT JOIN FETCH v.consolas " +
       "LEFT JOIN FETCH v.compania " +
       "WHERE uv.usuario.id = :usuarioId")
    List<UsuarioVideojuego> findWithVideojuegoCompletoByUsuarioId(@Param("usuarioId") Integer usuarioId);

    List<UsuarioVideojuego> findByUsuarioId(Integer usuarioId);

    List<UsuarioVideojuego> findByVideojuegoId(Integer videojuegoId);

    boolean existsByUsuarioIdAndVideojuegoId(Integer usuarioId, Integer videojuegoId);

    Optional<UsuarioVideojuego> findById(UsuarioVideojuegoID id);

    List<UsuarioVideojuego> findByUsuarioIdAndEnWishlist(Integer usuarioId, boolean enWishlist);
}

