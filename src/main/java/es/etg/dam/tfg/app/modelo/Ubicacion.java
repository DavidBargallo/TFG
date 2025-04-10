package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ubicacion")
@Data
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String lugar;
    private String zona;
    private String indicaciones;
}


