package es.etg.dam.tfg.programa.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.UsuarioVideojuegoRepositorio;
import jakarta.transaction.Transactional;

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
        return usuarioVideojuegoRepositorio.findWithVideojuegoCompletoByUsuarioId(usuarioId);
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

    @Transactional
    public List<UsuarioVideojuego> obtenerVideojuegosEnWishlist(Integer usuarioId) {
        return usuarioVideojuegoRepositorio.findConVideojuegoCompletoEnWishlist(usuarioId);
    }

    //
    @Transactional
    public UsuarioVideojuego guardar(UsuarioVideojuego relacion) {
        return usuarioVideojuegoRepositorio.save(relacion);
    }
    //
    
    @Transactional
    public void eliminarRelacionUsuarioVideojuego(Integer usuarioId, Integer videojuegoId) {
        UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuarioId, videojuegoId);
        usuarioVideojuegoRepositorio.deleteById(id);
    }
}
