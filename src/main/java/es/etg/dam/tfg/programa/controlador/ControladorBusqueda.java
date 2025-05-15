package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.*;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;
import es.etg.dam.tfg.programa.utils.AlertaUtils;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ControladorBusqueda {

    private final VideojuegoRepositorio videojuegoRepositorio;
    private final UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;
    private final RawgApiServicio rawgApiServicio;
    private final GeneroRepositorio generoRepositorio;
    private final ConsolaRepositorio consolaRepositorio;
    private final VideojuegoServicio videojuegoServicio;
    private int paginaActualApi = 1;
    private static final int JUEGOS_POR_PAGINA = 10;
    private Map<String, Integer> mapaGeneros = new HashMap<>();
    private Map<String, Integer> mapaConsolas = new HashMap<>();

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
    private Button btnAnteriorApi, btnSiguienteApi;
    @FXML
    private Label lblPaginaApi;
    @FXML
    private ComboBox<String> comboGeneroApi;

    @FXML
    public void initialize() {
        try {
            comboConsolaApi.getItems().clear();
            comboConsolaApi.getItems().add("Todas");
            for (JsonNode plataforma : rawgApiServicio.consumirApi("https://api.rawg.io/api/platforms")
                    .get("results")) {
                String nombre = plataforma.get("name").asText();
                int id = plataforma.get("id").asInt();
                comboConsolaApi.getItems().add(nombre);
                mapaConsolas.put(nombre, id);
            }
            comboConsolaApi.getSelectionModel().selectFirst();

            comboGeneroApi.getItems().clear();
            comboGeneroApi.getItems().add("Todos");
            for (JsonNode genero : rawgApiServicio.consumirApi("https://api.rawg.io/api/genres").get("results")) {
                String nombre = genero.get("name").asText();
                int id = genero.get("id").asInt();
                comboGeneroApi.getItems().add(nombre);
                mapaGeneros.put(nombre, id);
            }
            comboGeneroApi.getSelectionModel().selectFirst();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertaUtils.mostrarAlerta("Error al cargar géneros o plataformas desde la API.");
        }

        comboOrdenApi.getItems().addAll("Fecha más reciente", "Fecha más antigua");
        comboOrdenApi.getSelectionModel().selectFirst();

        btnBuscarApi.setOnAction(this::buscarJuegos);
    }

    private void buscarJuegos(ActionEvent event) {
        paginaActualApi = 1;
        cargarPaginaApi();
    }

    private void cargarPaginaApi() {
        String nombre = txtNombreApi.getText().trim();
        String generoSeleccionado = comboGeneroApi.getValue();
        String consolaSeleccionada = comboConsolaApi.getValue();
        String ordenSeleccionado = comboOrdenApi.getValue();

        Integer generoId = "Todos".equals(generoSeleccionado) ? null : mapaGeneros.get(generoSeleccionado);
        Integer plataformaId = "Todas".equals(consolaSeleccionada) ? null : mapaConsolas.get(consolaSeleccionada);

        try {
            JsonNode respuesta = rawgApiServicio.buscarJuegos(nombre, generoId, plataformaId, ordenSeleccionado,
                    paginaActualApi, JUEGOS_POR_PAGINA);

            JsonNode juegos = respuesta.get("results");
            contenedorResultadosApi.getChildren().clear();

            if (juegos != null && juegos.isArray() && juegos.size() > 0) {
                for (JsonNode juego : juegos) {
                    contenedorResultadosApi.getChildren().add(crearFichaJuego(juego));
                }
            } else {
                Label sinResultados = new Label("No se encontraron juegos con los filtros aplicados.");
                contenedorResultadosApi.getChildren().add(sinResultados);
            }

            lblPaginaApi.setText("Página " + paginaActualApi);
            btnAnteriorApi.setDisable(paginaActualApi == 1);
            boolean hayMasPaginas = respuesta.has("next") && !respuesta.get("next").isNull();
            btnSiguienteApi.setDisable(!hayMasPaginas);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertaUtils.mostrarAlerta("Error al cargar resultados: " + e.getMessage());
        }
    }

    @FXML
    private void paginaAnteriorApi() {
        if (paginaActualApi > 1) {
            paginaActualApi--;
            cargarPaginaApi();
        }
    }

    @FXML
    private void paginaSiguienteApi() {
        paginaActualApi++;
        cargarPaginaApi();
    }

    private HBox crearFichaJuego(JsonNode juegoJson) {
        String nombreJuego = juegoJson.get("name").asText();
        String imagenUrl = juegoJson.hasNonNull("background_image")
                ? juegoJson.get("background_image").asText()
                : null;

        LocalDate fechaLanzamiento = LocalDate.parse(
                juegoJson.hasNonNull("released") ? juegoJson.get("released").asText() : "2000-01-01");

        Image imagen;
        try {
            imagen = (imagenUrl != null && !imagenUrl.isBlank())
                    ? new Image(imagenUrl, 120, 80, true, true)
                    : new Image(getClass().getResource("/fotos/placeholder.png").toExternalForm(), 120, 80, true, true);
        } catch (Exception e) {
            imagen = new Image(getClass().getResource("/fotos/placeholder.png").toExternalForm(), 120, 80, true, true);
        }

        ImageView imageView = new ImageView(imagen);

        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        hBox.getChildren().addAll(imageView, new Label(nombreJuego));

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
                    AlertaUtils.mostrarAlerta("El juego ya está en tu biblioteca.");
                    return;
                }

                UsuarioVideojuego relacion = new UsuarioVideojuego();
                relacion.setId(id);
                relacion.setUsuario(usuario);
                relacion.setVideojuego(videojuego);
                relacion.setEnWishlist(false);

                usuarioVideojuegoRepositorio.save(relacion);

                AlertaUtils.mostrarAlerta("¡Juego agregado a tu biblioteca!");
                btnAgregar.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertaUtils.mostrarAlerta("Error al agregar el juego: " + ex.getMessage());
            }
        });

        return btnAgregar;
    }

    private Videojuego crearVideojuegoDesdeApi(VideojuegoTemp temp) {
        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(preguntarFormatoJuego());
        v.setGeneros(obtenerGeneros(temp.json()));
        v.setConsolas(Set.of(seleccionarConsola(temp.json())));
        return videojuegoServicio.guardar(v);
    }

    private Set<Genero> obtenerGeneros(JsonNode json) {
        Set<Genero> generos = new HashSet<>();
        if (json.has("genres")) {
            for (JsonNode generoNode : json.get("genres")) {
                String nombre = generoNode.get("name").asText();
                Genero genero = generoRepositorio.findByNombre(nombre).orElse(null);
                if (genero != null) {
                    generos.add(genero);
                }
            }
        }
        return generos;
    }

    private Consola seleccionarConsola(JsonNode json) {
        List<Consola> consolas = new ArrayList<>();
        if (json.has("platforms")) {
            for (JsonNode plataformaNode : json.get("platforms")) {
                String nombre = plataformaNode.get("platform").get("name").asText();
                consolaRepositorio.findByNombre(nombre).ifPresent(consolas::add);
            }
        }

        if (consolas.isEmpty()) {
            throw new RuntimeException("No se encontraron consolas válidas en la base de datos.");
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

    private boolean preguntarFormatoJuego() {
        List<String> opciones = List.of("Físico", "Digital");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Seleccionar formato");
        dialog.setHeaderText("¿Cómo tienes este juego?");
        dialog.setContentText("Formato:");

        Optional<String> seleccion = dialog.showAndWait();
        if (seleccion.isEmpty()) {
            throw new RuntimeException("Selección cancelada.");
        }

        return "Físico".equals(seleccion.get());
    }

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl, JsonNode json) {}
}
