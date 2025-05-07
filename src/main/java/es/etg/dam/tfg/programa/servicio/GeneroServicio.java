package es.etg.dam.tfg.programa.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.repositorio.GeneroRepositorio;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeneroServicio {

    private final GeneroRepositorio generoRepositorio;

    public List<Genero> obtenerTodos() {
        return generoRepositorio.findAll();
    }

    public Optional<Genero> obtenerPorId(Integer id) {
        return generoRepositorio.findById(id);
    }

    public Optional<Genero> obtenerPorNombre(String nombre) {
        return generoRepositorio.findByNombre(nombre);
    }
}
