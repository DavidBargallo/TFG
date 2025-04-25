package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Genero;

@Repository
public interface GeneroRepositorio extends JpaRepository<Genero, Integer> {
    boolean existsByNombre(String nombre);
}
