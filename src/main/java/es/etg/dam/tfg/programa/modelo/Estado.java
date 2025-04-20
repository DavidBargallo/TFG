package es.etg.dam.tfg.programa.modelo;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(
    name = "estado",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre_estado"})
)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "videojuegos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "nombre_estado", nullable = false)
    private String nombreEstado;

    private String descripcion;

    @OneToMany(mappedBy = "estado")
    private Set<Videojuego> videojuegos;
}





