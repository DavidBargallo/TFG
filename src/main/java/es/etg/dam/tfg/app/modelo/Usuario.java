package es.etg.dam.tfg.app.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    private String correo;
    private String contrasena;

    @ManyToMany
    @JoinTable(
        name = "usuario_videojuego",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "videojuego_id")
    )
    private Set<Videojuego> videojuegos;

    public void setContrasena(String contrasena) {
        this.contrasena = hashContrasena(contrasena);
    }

    //Hasheo de contrasena
    private String hashContrasena(String contrasena) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(contrasena);  
    }
}


