package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "estado")
@Data
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre_estado")
    private String nombreEstado;

    private String descripcion;

    @OneToMany(mappedBy = "estado")  
    private Set<Videojuego> videojuegos;
}



