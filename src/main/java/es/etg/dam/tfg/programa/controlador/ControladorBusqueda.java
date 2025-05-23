package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.*;
import es.etg.dam.tfg.programa.servicio.CompaniaServicio;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import es.etg.dam.tfg.programa.servicio.UbicacionServicio;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;
import es.etg.dam.tfg.programa.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ControladorBusqueda {

    private static final int JUEGOS_POR_PAGINA = 10;

    private final VideojuegoRepositorio videojuegoRepositorio;
    private final UsuarioVideojuegoRepositorio usuarioVideojuegoRepositorio;
    private final RawgApiServicio rawgApiServicio;
    private final GeneroRepositorio generoRepositorio;
    private final ConsolaRepositorio consolaRepositorio;
    private final VideojuegoServicio videojuegoServicio;
    private final CompaniaServicio companiaServicio;
    private final UbicacionServicio ubicacionServicio;
    private final ApplicationContext applicationContext;

    private int paginaActualApi = 1;
    private final Map<String, Integer> mapaGeneros = new HashMap<>();
    private final Map<String, Integer> mapaConsolas = new HashMap<>();

    @FXML
    private ComboBox<String> comboConsolaApi;
    @FXML
    private TextField txtNombreApi;
    @FXML
    private ComboBox<String> comboOrdenApi;
    @FXML
    private VBox contenedorResultadosApi;
    @FXML
    private Button btnBuscarApi, btnAnteriorApi, btnSiguienteApi;
    @FXML
    private Label lblPaginaApi;
    @FXML
    private ComboBox<String> comboGeneroApi;
    @FXML
    private MenuItem menuVolverBiblioteca;

    @FXML
    public void initialize() {
        menuVolverBiblioteca.setOnAction(e -> volverABiblioteca());
        cargarPlataformas();
        cargarGeneros();

        comboOrdenApi.getItems().addAll("Fecha más reciente", "Fecha más antigua");
        comboOrdenApi.getSelectionModel().selectFirst();

        btnBuscarApi.setOnAction(this::buscarJuegos);
    }

    private void cargarPlataformas() {
        try {
            JsonNode plataformas = rawgApiServicio.consumirApi("https://api.rawg.io/api/platforms").get("results");
            comboConsolaApi.getItems().clear();
            comboConsolaApi.getItems().add("Todas");

            plataformas.forEach(p -> {
                String nombre = p.get("name").asText();
                int id = p.get("id").asInt();
                comboConsolaApi.getItems().add(nombre);
                mapaConsolas.put(nombre, id);
            });

            comboConsolaApi.getSelectionModel().selectFirst();
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta("Error al cargar plataformas.");
        }
    }

    private void cargarGeneros() {
        try {
            JsonNode generos = rawgApiServicio.consumirApi("https://api.rawg.io/api/genres").get("results");
            comboGeneroApi.getItems().clear();
            comboGeneroApi.getItems().add("Todos");

            generos.forEach(g -> {
                String nombre = g.get("name").asText();
                int id = g.get("id").asInt();
                comboGeneroApi.getItems().add(nombre);
                mapaGeneros.put(nombre, id);
            });

            comboGeneroApi.getSelectionModel().selectFirst();
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta("Error al cargar géneros.");
        }
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
                juegos.forEach(j -> contenedorResultadosApi.getChildren().add(crearFichaJuego(j)));
            } else {
                contenedorResultadosApi.getChildren()
                        .add(new Label("No se encontraron juegos con los filtros aplicados."));
            }

            lblPaginaApi.setText("Página " + paginaActualApi);
            btnAnteriorApi.setDisable(paginaActualApi == 1);
            btnSiguienteApi.setDisable(!respuesta.has("next") || respuesta.get("next").isNull());

        } catch (IOException | InterruptedException e) {
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

    private void volverABiblioteca() {
        Stage actual = (Stage) contenedorResultadosApi.getScene().getWindow();
        FXMLSoporte.abrirEInicializar(
                applicationContext,
                RutaFXML.BIBLIOTECA,
                "Biblioteca",
                actual,
                BibliotecaControlador::inicializarBiblioteca);
    }

    private HBox crearFichaJuego(JsonNode juegoJson) {
        String nombreJuego = juegoJson.get("name").asText();
        String imagenUrl = juegoJson.hasNonNull("background_image")
                ? juegoJson.get("background_image").asText()
                : null;
        LocalDate fecha = juegoJson.hasNonNull("released")
                ? LocalDate.parse(juegoJson.get("released").asText())
                : LocalDate.of(1900, 1, 1);

        Image imagen = crearImagen(imagenUrl);
        ImageView imageView = new ImageView(imagen);
        HBox hBox = new HBox(10);
        hBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        hBox.getChildren().addAll(imageView, new Label(nombreJuego));

        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            Optional<Videojuego> existente = videojuegoRepositorio.findConRelacionesByNombreAndFecha(nombreJuego,
                    fecha);

            Optional<UsuarioVideojuego> uvOpt = existente
                    .map(v -> usuarioVideojuegoRepositorio
                            .findById(new UsuarioVideojuegoID(usuario.getId(), v.getId())))
                    .flatMap(v -> v);

            if (uvOpt.isPresent()) {
                UsuarioVideojuego relacion = uvOpt.get();
                if (!relacion.isEnWishlist()) {

                    return hBox;
                }

                Button btnAgregarBiblio = crearBotonAgregar(usuario, existente.orElse(null),
                        new VideojuegoTemp(nombreJuego, fecha, imagenUrl, juegoJson));
                hBox.getChildren().add(btnAgregarBiblio);

            } else {

                Button btnAgregarBiblio = crearBotonAgregar(usuario, null,
                        new VideojuegoTemp(nombreJuego, fecha, imagenUrl, juegoJson));
                Button btnWishlist = crearBotonWishlist(usuario, null,
                        new VideojuegoTemp(nombreJuego, fecha, imagenUrl, juegoJson));
                hBox.getChildren().addAll(btnAgregarBiblio, btnWishlist);
            }
        }

        return hBox;
    }

    private Image crearImagen(String url) {
        try {
            return (url != null && !url.isBlank())
                    ? new Image(url, 120, 80, true, true)
                    : new Image(getClass().getResource("/fotos/placeholder.png").toExternalForm(), 120, 80, true, true);
        } catch (Exception e) {
            return new Image(getClass().getResource("/fotos/placeholder.png").toExternalForm(), 120, 80, true, true);
        }
    }

    private Button crearBotonAgregar(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp temp) {
        Button btn = new Button("Agregar a biblioteca");

        btn.setOnAction(e -> {
            try {
                Videojuego juego = (juegoExistente != null) ? juegoExistente : crearVideojuegoDesdeApi(temp);

                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), juego.getId());
                Optional<UsuarioVideojuego> relacionExistente = usuarioVideojuegoRepositorio.findById(id);

                if (relacionExistente.isPresent()) {
                    UsuarioVideojuego relacion = relacionExistente.get();
                    if (!relacion.isEnWishlist()) {
                        AlertaUtils.mostrarAlerta("El juego ya está en tu biblioteca.");
                        return;
                    }

                    if (juego.getConsolas() == null || juego.getConsolas().isEmpty()) {
                        juego.setConsolas(Set.of(seleccionarConsola(temp.json())));
                    }
                    if (juego.getGeneros() == null || juego.getGeneros().isEmpty()) {
                        juego.setGeneros(obtenerGeneros(temp.json()));
                    }
                    if (juego.getCompania() == null && temp.json().has("developers")) {
                        for (JsonNode dev : temp.json().get("developers")) {
                            if (dev.hasNonNull("name")) {
                                String nombre = dev.get("name").asText();
                                Compania compania = companiaServicio.guardarSiNoExiste(nombre, "Desconocido", null,
                                        null);
                                if (compania != null) {
                                    juego.setCompania(compania);
                                    break;
                                }
                            }
                        }
                    }

                    boolean esFisico = preguntarFormatoJuego();
                    juego.setEsFisico(esFisico);
                    if (esFisico) {
                        juego.setUbicacion(obtenerUbicacionParaJuegoFisico());
                    }

                    videojuegoServicio.guardar(juego);

                    relacion.setEnWishlist(false);
                    usuarioVideojuegoRepositorio.save(relacion);

                    AlertaUtils.mostrarAlerta("Juego movido de wishlist a tu biblioteca.");
                } else {
                    boolean esFisico = preguntarFormatoJuego();
                    juego.setEsFisico(esFisico);
                    if (esFisico) {
                        juego.setUbicacion(obtenerUbicacionParaJuegoFisico());
                    }

                    juego.setConsolas(Set.of(seleccionarConsola(temp.json())));
                    videojuegoServicio.guardar(juego);

                    UsuarioVideojuego nuevaRelacion = new UsuarioVideojuego();
                    nuevaRelacion.setId(id);
                    nuevaRelacion.setUsuario(usuario);
                    nuevaRelacion.setVideojuego(juego);
                    nuevaRelacion.setEnWishlist(false);

                    usuarioVideojuegoRepositorio.save(nuevaRelacion);
                    AlertaUtils.mostrarAlerta("¡Juego agregado a tu biblioteca!");
                }

                btn.setVisible(false);

            } catch (Exception ex) {
                AlertaUtils.mostrarAlerta("Error al agregar el juego: " + ex.getMessage());
            }
        });

        return btn;
    }

    private Button crearBotonWishlist(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp temp) {
        Button btn = new Button("Agregar a wishlist");

        btn.setOnAction(e -> {
            try {
                Videojuego juego = (juegoExistente != null) ? juegoExistente : crearVideojuegoBasico(temp);
                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), juego.getId());

                Optional<UsuarioVideojuego> existente = usuarioVideojuegoRepositorio.findById(id);

                UsuarioVideojuego relacion;
                if (existente.isPresent()) {
                    relacion = existente.get();
                    if (relacion.isEnWishlist()) {
                        AlertaUtils.mostrarAlerta("El juego ya está en tu wishlist.");
                        return;
                    }
                    relacion.setEnWishlist(true);
                } else {
                    relacion = new UsuarioVideojuego();
                    relacion.setId(id);
                    relacion.setUsuario(usuario);
                    relacion.setVideojuego(juego);
                    relacion.setEnWishlist(true);
                }

                usuarioVideojuegoRepositorio.save(relacion);

                AlertaUtils.mostrarAlerta("¡Juego agregado a tu wishlist!");
                btn.setVisible(false);

            } catch (Exception ex) {
                AlertaUtils.mostrarAlerta("Error al agregar a wishlist: " + ex.getMessage());
            }
        });

        return btn;
    }

    private Videojuego crearVideojuegoBasico(VideojuegoTemp temp) {
        Optional<Videojuego> existente = videojuegoRepositorio.findByNombreAndFechaLanzamiento(temp.nombre(),
                temp.fecha());
        if (existente.isPresent())
            return existente.get();

        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(false);

        v.setGeneros(obtenerGeneros(temp.json()));

        if (temp.json().has("developers") && temp.json().get("developers").isArray()) {
            for (JsonNode dev : temp.json().get("developers")) {
                if (dev.hasNonNull("name")) {
                    String nombre = dev.get("name").asText();
                    Compania compania = companiaServicio.guardarSiNoExiste(nombre, "Desconocido", null, null);
                    if (compania != null) {
                        v.setCompania(compania);
                        break;
                    }
                }
            }
        }
        return videojuegoServicio.guardar(v);
    }

    private Videojuego crearVideojuegoDesdeApi(VideojuegoTemp temp) {
        Optional<Videojuego> existente = videojuegoRepositorio
                .findConRelacionesByNombreAndFecha(temp.nombre(), temp.fecha());
        if (existente.isPresent()) {
            return existente.get();
        }

        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(false);

        v.setGeneros(obtenerGeneros(temp.json()));
        //v.setConsolas(Set.of(seleccionarConsola(temp.json())));

        try {
            JsonNode juegoCompleto = rawgApiServicio
                    .consumirApi("https://api.rawg.io/api/games/" + temp.json().get("id").asInt());

            if (juegoCompleto.has("developers") && juegoCompleto.get("developers").isArray()) {
                for (JsonNode dev : juegoCompleto.get("developers")) {
                    if (dev.hasNonNull("name")) {
                        String nombre = dev.get("name").asText();
                        Compania compania = companiaServicio.guardarSiNoExiste(nombre, "Desconocido", null, null);
                        if (compania != null) {
                            v.setCompania(compania);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener información detallada del juego: " + e.getMessage());
        }

        return videojuegoServicio.guardar(v);
    }

    private Ubicacion obtenerUbicacionParaJuegoFisico() {
        List<Ubicacion> ubicaciones = ubicacionServicio.obtenerTodas();

        if (!ubicaciones.isEmpty()) {
            ChoiceDialog<Ubicacion> dialog = new ChoiceDialog<>(ubicaciones.get(0), ubicaciones);
            dialog.setTitle("Seleccionar ubicación");
            dialog.setHeaderText("Elige una ubicación ya registrada o cancela para crear una nueva:");
            dialog.setContentText("Ubicación:");

            Optional<Ubicacion> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
                return resultado.get();
            }
        }

        final Ubicacion[] nuevaUbicacion = new Ubicacion[1];

        Stage stage = new Stage();
        FXMLSoporte.abrirEInicializar(
                applicationContext,
                RutaFXML.NUEVA_UBICACION,
                "Nueva Ubicación",
                stage,
                (NuevaUbicacionControlador c) -> c.setOnUbicacionGuardada(u -> {
                    nuevaUbicacion[0] = u;
                    stage.close();
                }));

        stage.showAndWait();

        if (nuevaUbicacion[0] != null) {
            return nuevaUbicacion[0];
        }

        AlertaUtils.mostrarAlerta("Debes seleccionar o crear una ubicación para un juego físico.");
        throw new RuntimeException("Ubicación no establecida para juego físico.");
    }

    private Set<Genero> obtenerGeneros(JsonNode json) {
        Set<Genero> generos = new HashSet<>();
        if (json.has("genres")) {
            json.get("genres").forEach(node -> {
                String nombre = node.get("name").asText();
                generoRepositorio.findByNombre(nombre).ifPresent(generos::add);
            });
        }
        return generos;
    }

    private Consola seleccionarConsola(JsonNode json) {
        List<Consola> consolas = new ArrayList<>();
        if (json.has("platforms")) {
            json.get("platforms").forEach(n -> {
                String nombre = n.get("platform").get("name").asText();
                consolaRepositorio.findByNombre(nombre).ifPresent(consolas::add);
            });
        }

        if (consolas.isEmpty())
            throw new RuntimeException("No se encontraron consolas válidas.");

        if (consolas.size() == 1)
            return consolas.get(0);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(consolas.get(0).getNombre(),
                consolas.stream().map(Consola::getNombre).toList());
        dialog.setTitle("Seleccionar consola");
        dialog.setHeaderText("Elige una consola para agregar el juego:");
        dialog.setContentText("Consola:");

        return dialog.showAndWait()
                .flatMap(nombre -> consolas.stream().filter(c -> c.getNombre().equals(nombre)).findFirst())
                .orElseThrow(() -> new RuntimeException("Consola no encontrada."));
    }

    private boolean preguntarFormatoJuego() {
        List<String> opciones = List.of("Físico", "Digital");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Físico", opciones);
        dialog.setTitle("Seleccionar formato");
        dialog.setHeaderText("¿Cómo tienes este juego?");
        dialog.setContentText("Formato:");
        return dialog.showAndWait().orElseThrow(() -> new RuntimeException("Selección cancelada.")).equals("Físico");
    }

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl, JsonNode json) {
    }
}