package es.etg.dam.tfg.app.modelo;

import es.etg.dam.tfg.app.modelo.ids.UsuarioVideojuegoID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "usuario_videojuego")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioVideojuego {

    @EmbeddedId
    private UsuarioVideojuegoID id = new UsuarioVideojuegoID();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videojuegoId")
    @JoinColumn(name = "videojuego_id", nullable = false)
    private Videojuego videojuego;

    @Builder.Default
    @Column(name = "en_wishlist", nullable = false)
    private boolean enWishlist = false;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "precio_pagado", precision = 10, scale = 2)
    @Min(0)
    private BigDecimal precioPagado;

    @Column(name = "horas_jugadas")
    @Min(0)
    private Integer horasJugadas;
}
