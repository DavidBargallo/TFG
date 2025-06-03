package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.repositorio.VideojuegoRepositorio;
import es.etg.dam.tfg.programa.repositorio.GeneroRepositorio;
import es.etg.dam.tfg.programa.repositorio.ConsolaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VideojuegoServicio {

    private final VideojuegoRepositorio videojuegoRepositorio;
    private final GeneroRepositorio generoRepositorio;
    private final ConsolaRepositorio consolaRepositorio;

    @Autowired
    public VideojuegoServicio(VideojuegoRepositorio videojuegoRepositorio, GeneroRepositorio generoRepositorio,
            ConsolaRepositorio consolaRepositorio) {
        this.videojuegoRepositorio = videojuegoRepositorio;
        this.generoRepositorio = generoRepositorio;
        this.consolaRepositorio = consolaRepositorio;
    }

    @Transactional
    public Videojuego guardar(Videojuego videojuego) {
        Set<Genero> managedGeneros = new HashSet<>();
        if (videojuego.getGeneros() != null) {
            for (Genero genero : videojuego.getGeneros()) {
                generoRepositorio.findByNombre(genero.getNombre())
                        .ifPresent(managedGeneros::add);
            }
            videojuego.setGeneros(managedGeneros);
        }

        Set<Consola> managedConsolas = new HashSet<>();
        if (videojuego.getConsolas() != null) {
            for (Consola consola : videojuego.getConsolas()) {
                consolaRepositorio.findByNombre(consola.getNombre())
                        .ifPresent(managedConsolas::add);
            }
            videojuego.setConsolas(managedConsolas);
        }

        Videojuego guardado = videojuegoRepositorio.save(videojuego);
        return videojuegoRepositorio.findById(guardado.getId()).orElse(null);
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

    //
    public Optional<Videojuego> obtenerConRelacionesPorNombreYFecha(String nombre, LocalDate fechaLanzamiento) {
        return videojuegoRepositorio.findConRelacionesByNombreAndFecha(nombre, fechaLanzamiento);
    }

    //

    @Transactional(readOnly = true)
    public List<Videojuego> buscarPorNombreConteniendo(String nombre) {
        return videojuegoRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public void borrar(Integer id) {
        videojuegoRepositorio.deleteById(id);
    }
}
