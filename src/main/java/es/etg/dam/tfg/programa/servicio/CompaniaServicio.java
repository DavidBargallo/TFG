package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.modelo.Compania;
import es.etg.dam.tfg.programa.repositorio.CompaniaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompaniaServicio {

    private final CompaniaRepositorio companiaRepositorio;

    public Optional<Compania> obtenerPorNombreYPais(String nombre, String pais) {
        return companiaRepositorio.findByNombreAndPais(nombre, pais);
    }

    public Compania guardarSiNoExiste(String nombre, String pais, Integer anioFundacion, String sitioWeb) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("⚠️ No se puede guardar una compañía sin nombre.");
            return null;
        }

        if (pais == null || pais.isBlank()) {
            pais = "Desconocido";
        }

        Optional<Compania> existente = companiaRepositorio.findByNombreAndPais(nombre, pais);
        if (existente.isPresent()) {
            System.out.println("✅ Compañía ya existente encontrada: " + existente.get().getNombre() + " ("
                    + existente.get().getId() + ")");
            return existente.get();
        }

        Compania nueva = new Compania();
        nueva.setNombre(nombre);
        nueva.setPais(pais);
        nueva.setAnioFundacion(anioFundacion != null ? anioFundacion : 0);
        nueva.setSitioWeb(sitioWeb);

        Compania guardada = companiaRepositorio.saveAndFlush(nueva);
        System.out.println("✅ Nueva compañía guardada: " + guardada.getNombre() + " (ID: " + guardada.getId() + ")");

        return guardada;
    }

}
