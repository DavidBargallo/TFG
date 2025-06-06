package es.etg.dam.tfg.programa.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(
    name = "videojuego",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "fecha_lanzamiento"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate fechaLanzamiento;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    @ManyToOne
    @JoinColumn(name = "compania_id")
    private Compania compania;

    @Column(name = "portada_url")
    private String portadaUrl;

    @ManyToMany
    @JoinTable(
        name = "videojuego_genero",
        joinColumns = @JoinColumn(name = "videojuego_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<Genero> generos;

    @ManyToMany
    @JoinTable(
        name = "videojuego_consola",
        joinColumns = @JoinColumn(name = "videojuego_id"),
        inverseJoinColumns = @JoinColumn(name = "consola_id")
    )
    private Set<Consola> consolas;

    @Column(name = "es_fisico", nullable = false)
    private boolean esFisico;

}