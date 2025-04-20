package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Estado;

public interface EstadoRepositorio extends JpaRepository<Estado, Integer> {

    boolean existsByNombreEstado(String nombreEstado);
}

