package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(
    name = "genero",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre"})
)
@Data
@ToString(exclude = "videojuegos") 
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToMany(mappedBy = "generos")
    private Set<Videojuego> videojuegos;
}





