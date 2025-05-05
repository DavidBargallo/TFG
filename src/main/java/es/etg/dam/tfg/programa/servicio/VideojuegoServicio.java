package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.repositorio.VideojuegoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoServicio {

    private final VideojuegoRepositorio videojuegoRepositorio;

    @Autowired
    public VideojuegoServicio(VideojuegoRepositorio videojuegoRepositorio) {
        this.videojuegoRepositorio = videojuegoRepositorio;
    }

    @Transactional
    public Videojuego guardar(Videojuego videojuego) {
        return videojuegoRepositorio.save(videojuego);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> obtenerTodos() {
        return videojuegoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Videojuego> obtenerPorId(Integer id) {
        return videojuegoRepositorio.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Videojuego> obtenerPorNombre(String nombre) {
        return videojuegoRepositorio.findByNombre(nombre);
    }

     @Transactional(readOnly = true)
    public Optional<Videojuego> obtenerPorNombreYFechaLanzamiento(String nombre, LocalDate fechaLanzamiento) {
        return videojuegoRepositorio.findByNombreAndFechaLanzamiento(nombre, fechaLanzamiento);
    }

     @Transactional(readOnly = true)
    public List<Videojuego> buscarPorNombreConteniendo(String nombre) {
        return videojuegoRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public void borrar(Integer id) {
        videojuegoRepositorio.deleteById(id);
    }
}

