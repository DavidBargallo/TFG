package es.etg.dam.tfg.programa.modelo;

import es.etg.dam.tfg.programa.modelo.ids.VideojuegoConsolaID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "videojuego_consola")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VideojuegoConsolaID.class)
public class VideojuegoConsola {

    @Id
    @ManyToOne
    @JoinColumn(name = "videojuego_id")
    private Videojuego videojuego;

    @Id
    @ManyToOne
    @JoinColumn(name = "consola_id")
    private Consola consola;
}

