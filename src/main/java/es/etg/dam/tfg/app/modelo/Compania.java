package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "compania",
    uniqueConstraints = @UniqueConstraint(columnNames = { "nombre", "pais" })
)
@Data
public class Compania {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    private String pais;

    @Column(name = "anio_fundacion")
    private int anioFundacion;

    @Column(name = "sitio_web")
    private String sitioWeb;
}

