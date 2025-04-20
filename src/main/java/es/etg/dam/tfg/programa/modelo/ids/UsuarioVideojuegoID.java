package es.etg.dam.tfg.programa.modelo.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioVideojuegoID implements Serializable {

    private Integer usuarioId;
    private Integer videojuegoId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UsuarioVideojuegoID uvID)) return false;
        return Objects.equals(usuarioId, uvID.usuarioId) &&
               Objects.equals(videojuegoId, uvID.videojuegoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, videojuegoId);
    }
}

