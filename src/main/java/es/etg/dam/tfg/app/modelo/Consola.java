package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "consola")
@Data 
public class Consola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "fabricante_id")  
    private Fabricante fabricante;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @ManyToMany(mappedBy = "consolas") 
    private Set<Videojuego> videojuegos;
}


