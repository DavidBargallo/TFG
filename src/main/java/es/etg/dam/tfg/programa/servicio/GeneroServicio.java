package es.etg.dam.tfg.programa.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.repositorio.GeneroRepositorio;

@Service
public class GeneroServicio {

    private final GeneroRepositorio generoRepositorio;

    @Autowired
    public GeneroServicio(GeneroRepositorio generoRepositorio) {
        this.generoRepositorio = generoRepositorio;
    }

    @Transactional
    public Genero guardar(Genero genero) {
        return generoRepositorio.save(genero);
    }

    @Transactional(readOnly = true)
    public List<Genero> obtenerTodos() {
        return generoRepositorio.findAll();
    }

}
