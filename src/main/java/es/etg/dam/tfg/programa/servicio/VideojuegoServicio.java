package es.etg.dam.tfg.programa.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.repositorio.VideojuegoRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideojuegoServicio {

    private final VideojuegoRepositorio videojuegoRepositorio;

    public List<Videojuego> obtenerTodos() {
        return videojuegoRepositorio.findAll();
    }

    public Optional<Videojuego> obtenerPorId(Integer id) {
        return videojuegoRepositorio.findById(id);
    }

    public boolean existePorNombreYFecha(String nombre, LocalDate fechaLanzamiento) {
        return videojuegoRepositorio.existsByNombreAndFechaLanzamiento(nombre, fechaLanzamiento);
    }

    public Optional<Videojuego> buscarPorNombreYFecha(String nombre, LocalDate fechaLanzamiento) {
        return videojuegoRepositorio.findByNombreAndFechaLanzamiento(nombre, fechaLanzamiento);
    }

    public Videojuego guardar(Videojuego videojuego) {
        return videojuegoRepositorio.save(videojuego);
    }

    public void eliminarPorId(Integer id) {
        videojuegoRepositorio.deleteById(id);
    }
}
