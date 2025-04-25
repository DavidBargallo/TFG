package es.etg.dam.tfg.programa.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(
    name = "usuario",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre_usuario"}),
        @UniqueConstraint(columnNames = {"correo"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "nombre_usuario", nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @ManyToMany
    @JoinTable(
        name = "usuario_videojuego",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "videojuego_id")
    )
    private Set<Videojuego> videojuegos;
}
