package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Integer> {
    boolean existsByLugarAndZonaAndIndicaciones(String lugar, String zona, String indicaciones);
}
