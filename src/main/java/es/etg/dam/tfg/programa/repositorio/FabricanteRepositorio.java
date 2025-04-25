package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Fabricante;

@Repository
public interface FabricanteRepositorio extends JpaRepository<Fabricante, Integer> {
}

