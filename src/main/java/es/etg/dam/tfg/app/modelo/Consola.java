package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(
    name = "consola",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "fecha_lanzamiento"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "videojuegos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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



