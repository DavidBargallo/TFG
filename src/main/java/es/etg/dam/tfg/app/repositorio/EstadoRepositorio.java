package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepositorio extends JpaRepository<Estado, Integer> {

    boolean existsByNombreEstado(String nombreEstado);
}

