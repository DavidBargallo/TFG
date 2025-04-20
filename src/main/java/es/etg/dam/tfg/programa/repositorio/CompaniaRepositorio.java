package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Compania;

import java.util.Optional;

public interface CompaniaRepositorio extends JpaRepository<Compania, Integer> {

    boolean existsByNombreAndPais(String nombre, String pais);

    Optional<Compania> findByNombreAndPais(String nombre, String pais);
}
