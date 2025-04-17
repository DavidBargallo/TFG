package es.etg.dam.tfg.app.servicio;

import es.etg.dam.tfg.app.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.app.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.app.repositorio.UsuarioVideojuegoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioVideojuegoServicio {

    private final UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;

    @Autowired
    public UsuarioVideojuegoServicio(UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio) {
        this.usuarioVideojuegoRepositorio = usuarioVideojuegoRepositorio;
    }

    public List<UsuarioVideojuego> obtenerVideojuegosPorUsuario(Integer usuarioId) {
        return usuarioVideojuegoRepositorio.findByUsuarioId(usuarioId);
    }

    public boolean tieneVideojuego(Integer usuarioId, Integer videojuegoId) {
        return usuarioVideojuegoRepositorio.existsByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId);
    }

    public void agregarVideojuegoAUsuario(UsuarioVideojuego usuarioVideojuego) {
        usuarioVideojuegoRepositorio.save(usuarioVideojuego);
    }

    public Optional<UsuarioVideojuego> obtenerVideojuegoPorId(UsuarioVideojuegoID id) {
        return usuarioVideojuegoRepositorio.findById(id);
    }

    public List<UsuarioVideojuego> obtenerVideojuegosEnWishlist(Integer usuarioId) {
        return usuarioVideojuegoRepositorio.findByUsuarioIdAndEnWishlist(usuarioId, true);
    }
}

