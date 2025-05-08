package es.etg.dam.tfg.programa.modelo;

import es.etg.dam.tfg.programa.modelo.ids.VideojuegoGeneroID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "videojuego_genero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VideojuegoGeneroID.class) // Usa la clase de ID compuesta
public class VideojuegoGenero {

    @Id
    @ManyToOne
    @JoinColumn(name = "videojuego_id")
    private Videojuego videojuego;

    @Id
    @ManyToOne
    @JoinColumn(name = "genero_id")
    private Genero genero;
}

