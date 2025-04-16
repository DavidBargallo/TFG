package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Compania;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompaniaRepositorio extends JpaRepository<Compania, Integer> {

    boolean existsByNombreAndPais(String nombre, String pais);

    Optional<Compania> findByNombreAndPais(String nombre, String pais);
}
