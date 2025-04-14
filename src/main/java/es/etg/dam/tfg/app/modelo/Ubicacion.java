package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ubicacion", uniqueConstraints = @UniqueConstraint(columnNames = { "lugar", "zona", "indicaciones" }))
@Data
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String lugar;

    private String zona;
    private String indicaciones;
}
