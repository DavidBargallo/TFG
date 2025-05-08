package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.*;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;
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
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ControladorBusqueda {

    private final VideojuegoRepositorio videojuegoRepositorio;
    private final UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;
    private final RawgApiServicio rawgApiServicio;
    private final GeneroRepositorio generoRepositorio;
    private final ConsolaRepositorio consolaRepositorio;
    private final VideojuegoServicio videojuegoServicio;

    @FXML
    private ComboBox<String> comboConsolaApi;
    @FXML
    private TextField txtNombreApi;
    @FXML
    private TextField txtPrecioMaxApi;
    @FXML
    private ComboBox<String> comboOrdenApi;
    @FXML
    private VBox contenedorResultadosApi;
    @FXML
    private Button btnBuscarApi;

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
            contenedorResultadosApi.getChildren().clear();

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
        LocalDate fechaLanzamiento = LocalDate.parse(
                juegoJson.hasNonNull("released") ? juegoJson.get("released").asText() : "2000-01-01");

        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        hBox.getChildren().addAll(
                new ImageView(new Image(imagenUrl, 120, 80, true, true)),
                new Label(nombreJuego));

        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            Optional<Videojuego> existente = videojuegoRepositorio.findByNombreAndFechaLanzamiento(nombreJuego,
                    fechaLanzamiento);
            boolean yaEnBiblioteca = existente
                    .map(v -> usuarioVideojuegoRepositorio
                            .existsById(new UsuarioVideojuegoID(usuario.getId(), v.getId())))
                    .orElse(false);

            if (!yaEnBiblioteca) {
                Button btnAgregar = crearBotonAgregar(usuario, existente.orElse(null),
                        new VideojuegoTemp(nombreJuego, fechaLanzamiento, imagenUrl, juegoJson));
                hBox.getChildren().add(btnAgregar);
            }
        }

        return hBox;
    }

    private Button crearBotonAgregar(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp juegoNuevo) {
        Button btnAgregar = new Button("Agregar a biblioteca");
        btnAgregar.setOnAction(e -> {
            try {
                Videojuego videojuego = (juegoExistente != null) ? juegoExistente : crearVideojuegoDesdeApi(juegoNuevo);
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
                btnAgregar.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al agregar el juego: " + ex.getMessage());
            }
        });

        return btnAgregar;
    }

    private Videojuego crearVideojuegoDesdeApi(VideojuegoTemp temp) {
        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(false);
        v.setGeneros(obtenerGeneros(temp.json()));
        v.setConsolas(Set.of(seleccionarConsola(temp.json())));
        return videojuegoServicio.guardar(v);
    }

    private Set<Genero> obtenerGeneros(JsonNode json) {
        Set<Genero> generos = new HashSet<>();
        if (json.has("genres")) {
            for (JsonNode generoNode : json.get("genres")) {
                String nombre = generoNode.get("name").asText();
                Genero genero = generoRepositorio.findByNombre(nombre).orElseGet(() -> {
                    Genero nuevo = new Genero();
                    nuevo.setNombre(nombre);
                    nuevo.setDescripcion("");
                    return generoRepositorio.save(nuevo);
                });
                generos.add(genero);
            }
        }
        return generos;
    }

    private Consola seleccionarConsola(JsonNode json) {
        List<Consola> consolas = new ArrayList<>();
        if (json.has("platforms")) {
            for (JsonNode plataformaNode : json.get("platforms")) {
                String nombre = plataformaNode.get("platform").get("name").asText();
                Consola consola = consolaRepositorio.findByNombre(nombre).orElseGet(() -> {
                    Consola nueva = new Consola();
                    nueva.setNombre(nombre);
                    nueva.setFechaLanzamiento(null);
                    nueva.setFabricante(null);
                    return consolaRepositorio.save(nueva);
                });
                consolas.add(consola);
            }
        }

        if (consolas.isEmpty()) {
            throw new RuntimeException("No se encontraron consolas en la información del juego.");
        }

        if (consolas.size() == 1) {
            return consolas.get(0);
        }

        List<String> nombres = consolas.stream().map(Consola::getNombre).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Seleccionar consola");
        dialog.setHeaderText("Elige una consola para agregar el juego:");
        dialog.setContentText("Consola:");

        Optional<String> seleccion = dialog.showAndWait();
        if (seleccion.isEmpty())
            throw new RuntimeException("Selección cancelada.");

        return consolas.stream()
                .filter(c -> c.getNombre().equals(seleccion.get()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Consola no encontrada."));
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl, JsonNode json) {
    }
}
