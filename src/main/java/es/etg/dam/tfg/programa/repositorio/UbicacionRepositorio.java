package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Ubicacion;

@Repository
public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Integer> {
    boolean existsByLugarAndZonaAndIndicaciones(String lugar, String zona, String indicaciones);
}
