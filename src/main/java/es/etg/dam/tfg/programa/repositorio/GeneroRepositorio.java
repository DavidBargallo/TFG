package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Genero;

public interface GeneroRepositorio extends JpaRepository<Genero, Integer> {
    boolean existsByNombre(String nombre);
}
