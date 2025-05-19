package es.etg.dam.tfg.programa.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "compania"
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

