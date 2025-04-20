package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import es.etg.dam.tfg.programa.modelo.Fabricante;

public interface FabricanteRepositorio extends JpaRepository<Fabricante, Integer> {
}

