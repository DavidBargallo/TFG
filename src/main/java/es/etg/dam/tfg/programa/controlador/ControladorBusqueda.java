package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.UsuarioVideojuegoRepositorio;
import es.etg.dam.tfg.programa.repositorio.VideojuegoRepositorio;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ControladorBusqueda {

    private final VideojuegoRepositorio videojuegoRepositorio;
    private final UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;
    private final RawgApiServicio rawgApiServicio;

    @FXML private ComboBox<String> comboConsolaApi;
    @FXML private TextField txtNombreApi;
    @FXML private TextField txtPrecioMaxApi;
    @FXML private ComboBox<String> comboOrdenApi;
    @FXML private VBox contenedorResultadosApi;
    @FXML private Button btnBuscarApi;

    @FXML
    public void initialize() {
        comboConsolaApi.getItems().addAll("PC", "PlayStation", "Xbox", "Nintendo", "Mobile");
        comboOrdenApi.getItems().addAll("Relevancia", "Precio", "Fecha de lanzamiento");
        btnBuscarApi.setOnAction(this::buscarJuegos);
    }

    private void buscarJuegos(ActionEvent event) {
    String nombre = txtNombreApi.getText().trim();

    if (nombre.isEmpty()) {
        mostrarAlerta("Por favor, introduce un nombre para buscar.");
        return;
    }

    try {
        JsonNode juegos = rawgApiServicio.buscarJuegos(nombre);
        contenedorResultadosApi.getChildren().clear(); // Limpiar anteriores

        for (JsonNode juego : juegos) {
            contenedorResultadosApi.getChildren().add(crearFichaJuego(juego));
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        mostrarAlerta("Error al buscar juegos: " + e.getMessage());
    }
}


    private HBox crearFichaJuego(JsonNode juegoJson) {
        String nombreJuego = juegoJson.get("name").asText();
        String imagenUrl = juegoJson.get("background_image").asText();
        String fechaStr = juegoJson.hasNonNull("released") ? juegoJson.get("released").asText() : "2000-01-01";
        LocalDate fechaLanzamiento = LocalDate.parse(fechaStr);

        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");

        ImageView imageView = new ImageView(new Image(imagenUrl, 120, 80, true, true));
        Label labelJuego = new Label(nombreJuego);

        hBox.getChildren().addAll(imageView, labelJuego);

        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            Optional<Videojuego> existente = videojuegoRepositorio.findByNombreAndFechaLanzamiento(nombreJuego, fechaLanzamiento);

            if (existente.isPresent()) {
                Videojuego juegoBD = existente.get();
                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), juegoBD.getId());

                if (!usuarioVideojuegoRepositorio.existsById(id)) {
                    Button btnAgregar = crearBotonAgregar(usuario, juegoBD, null);
                    hBox.getChildren().add(btnAgregar);
                }
            } else {
                Button btnAgregar = crearBotonAgregar(usuario, null, new VideojuegoTemp(nombreJuego, fechaLanzamiento, imagenUrl));
                hBox.getChildren().add(btnAgregar);
            }
        }

        return hBox;
    }

    private Button crearBotonAgregar(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp juegoNuevo) {
        Button btnAgregar = new Button("Agregar a biblioteca");
        btnAgregar.setOnAction(e -> {
            try {
                Videojuego videojuego = juegoExistente;
                if (videojuego == null && juegoNuevo != null) {
                    videojuego = new Videojuego();
                    videojuego.setNombre(juegoNuevo.nombre());
                    videojuego.setFechaLanzamiento(juegoNuevo.fecha());
                    videojuego.setPortadaUrl(juegoNuevo.imagenUrl());
                    videojuego.setEsFisico(false);
                    videojuego = videojuegoRepositorio.save(videojuego);
                }
    
                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), videojuego.getId());
                if (usuarioVideojuegoRepositorio.existsById(id)) {
                    mostrarAlerta("El juego ya está en tu biblioteca.");
                    return;
                }
    
                UsuarioVideojuego relacion = new UsuarioVideojuego();
                relacion.setId(id);
                relacion.setUsuario(usuario);
                relacion.setVideojuego(videojuego);
                relacion.setEnWishlist(false);
                usuarioVideojuegoRepositorio.save(relacion);
    
                mostrarAlerta("¡Juego agregado a tu biblioteca!");
            } catch (Exception ex) {
                mostrarAlerta("Error al agregar el juego: " + ex.getMessage());
            }
        });
    
        return btnAgregar;
    }
    

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl) {}
}
