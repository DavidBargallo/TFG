package es.etg.dam.tfg.programa.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(
    name = "fabricante",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre"})
)
@Data
public class Fabricante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "fabricante")
    private Set<Consola> consolas;
}



