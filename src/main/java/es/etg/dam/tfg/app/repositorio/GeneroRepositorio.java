package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneroRepositorio extends JpaRepository<Genero, Integer> {
    boolean existsByNombre(String nombre);
}
