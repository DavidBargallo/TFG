package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "genero")
@Data
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String descripcion;

    @ManyToMany(mappedBy = "generos") 
    private Set<Videojuego> videojuegos;
}



