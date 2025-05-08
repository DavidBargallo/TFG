package es.etg.dam.tfg.programa.modelo.ids;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideojuegoConsolaID implements Serializable{
    private int videojuego;
    private int consola;
}
