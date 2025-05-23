package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Estado;

@Repository
public interface EstadoRepositorio extends JpaRepository<Estado, Integer> {

    boolean existsByNombreEstado(String nombreEstado);
}

