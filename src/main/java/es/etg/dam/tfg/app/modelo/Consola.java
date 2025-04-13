package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(
    //Para que no se repitan consolas.
    name = "consola",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "fecha_lanzamiento"})
)
@Data 
public class Consola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "fabricante_id")  
    private Fabricante fabricante;

    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate fechaLanzamiento;

    @ManyToMany(mappedBy = "consolas") 
    private Set<Videojuego> videojuegos;
}



