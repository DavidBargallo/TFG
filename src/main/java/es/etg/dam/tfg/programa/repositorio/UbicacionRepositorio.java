package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Ubicacion;

public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Integer> {
    boolean existsByLugarAndZonaAndIndicaciones(String lugar, String zona, String indicaciones);
}
