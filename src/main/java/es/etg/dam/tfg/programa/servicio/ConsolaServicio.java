package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.repositorio.ConsolaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsolaServicio {

    private final ConsolaRepositorio consolaRepositorio;

    public List<Consola> obtenerTodas() {
        return consolaRepositorio.findAll();
    }


    public Optional<Consola> obtenerPorId(Integer id) {
        return consolaRepositorio.findById(id);
    }

    public Consola guardar(Consola consola) {
        return consolaRepositorio.save(consola);
    }

    public void eliminarPorId(Integer id) {
        consolaRepositorio.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return consolaRepositorio.existsByNombreIgnoreCase(nombre);
    }

}

