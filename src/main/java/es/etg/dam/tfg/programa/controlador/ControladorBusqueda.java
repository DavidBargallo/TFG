package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
import es.etg.dam.tfg.programa.repositorio.*;
import es.etg.dam.tfg.programa.servicio.*;
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
    private final ConsolaServicio consolaServicio;
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

        comboOrdenApi.getItems().addAll("Ninguno", "Fecha más reciente", "Fecha más antigua");
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
        String nombre = txtNombreApi.getText().trim().toLowerCase();
        String generoSeleccionado = comboGeneroApi.getValue();
        String consolaSeleccionada = comboConsolaApi.getValue();
        String ordenSeleccionado = comboOrdenApi.getValue();

        Integer generoId = "Todos".equals(generoSeleccionado) ? null : mapaGeneros.get(generoSeleccionado);
        Integer plataformaId = "Todas".equals(consolaSeleccionada) ? null : mapaConsolas.get(consolaSeleccionada);
        String ordenFinal = "Ninguno".equals(ordenSeleccionado) ? null : ordenSeleccionado;

        try {
            JsonNode respuesta = rawgApiServicio.buscarJuegos(nombre, generoId, plataformaId, ordenFinal,
                    paginaActualApi, JUEGOS_POR_PAGINA);

            JsonNode juegos = respuesta.get("results");
            contenedorResultadosApi.getChildren().clear();

            if (juegos != null && juegos.isArray() && juegos.size() > 0) {
                List<JsonNode> listaJuegos = new ArrayList<>();
                juegos.forEach(listaJuegos::add);

                listaJuegos.removeIf(juego -> {
                    if (!"Todas".equals(consolaSeleccionada) && !tienePlataforma(juego, consolaSeleccionada)) {
                        return true;
                    }
                    if (!"Todos".equals(generoSeleccionado) && !tieneGenero(juego, generoSeleccionado)) {
                        return true;
                    }
                    return false;
                });

                listaJuegos.sort(
                        Comparator
                                .comparing((JsonNode j) -> !j.get("name").asText().toLowerCase().contains(nombre))

                                .thenComparing((JsonNode j1, JsonNode j2) -> {
                                    if ("Fecha más reciente".equals(ordenSeleccionado)) {
                                        return extraerFecha(j2).compareTo(extraerFecha(j1));
                                    } else if ("Fecha más antigua".equals(ordenSeleccionado)) {
                                        return extraerFecha(j1).compareTo(extraerFecha(j2));
                                    }
                                    return 0;
                                }));

                if (listaJuegos.isEmpty()) {
                    contenedorResultadosApi.getChildren()
                            .add(new Label("No se encontraron juegos con los filtros aplicados."));
                } else {
                    listaJuegos.forEach(j -> contenedorResultadosApi.getChildren().add(crearFichaJuego(j)));
                }

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

    private boolean tieneGenero(JsonNode juego, String generoSeleccionado) {
        if (generoSeleccionado == null || generoSeleccionado.equals("Todos"))
            return true;
        if (!juego.has("genres"))
            return false;

        for (JsonNode gen : juego.get("genres")) {
            String nombre = gen.get("name").asText();
            if (generoSeleccionado.equalsIgnoreCase(nombre))
                return true;
        }
        return false;
    }

    private boolean tienePlataforma(JsonNode juego, String consolaSeleccionada) {
        if (consolaSeleccionada == null || consolaSeleccionada.equals("Todas"))
            return false;
        if (!juego.has("platforms"))
            return false;

        for (JsonNode plat : juego.get("platforms")) {
            String nombre = plat.get("platform").get("name").asText();
            if (consolaSeleccionada.equalsIgnoreCase(nombre))
                return true;
        }
        return false;
    }

    private LocalDate extraerFecha(JsonNode juego) {
        if (juego.hasNonNull("released")) {
            try {
                return LocalDate.parse(juego.get("released").asText());
            } catch (Exception e) {
                return LocalDate.of(1900, 1, 1);
            }
        }
        return LocalDate.of(1900, 1, 1);
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
                        juego.setConsolas(
                                Set.of(FormularioJuegoUtils.seleccionarConsolaDesdeJson(temp.json(), consolaServicio)));
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

                    boolean esFisico = FormularioJuegoUtils.preguntarFormatoJuego();
                    juego.setEsFisico(esFisico);
                    if (esFisico) {
                        juego.setUbicacion(FormularioJuegoUtils.obtenerUbicacionParaJuegoFisico(ubicacionServicio,
                                applicationContext));
                    }

                    videojuegoServicio.guardar(juego);

                    relacion.setEnWishlist(false);
                    usuarioVideojuegoRepositorio.save(relacion);

                    AlertaUtils.mostrarAlerta("Juego movido de wishlist a tu biblioteca.");
                } else {
                    boolean esFisico = FormularioJuegoUtils.preguntarFormatoJuego();
                    juego.setEsFisico(esFisico);
                    if (esFisico) {
                        juego.setUbicacion(FormularioJuegoUtils.obtenerUbicacionParaJuegoFisico(ubicacionServicio,
                                applicationContext));
                    }

                    juego.setConsolas(
                            Set.of(FormularioJuegoUtils.seleccionarConsolaDesdeJson(temp.json(), consolaServicio)));
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
        // v.setConsolas(Set.of(seleccionarConsola(temp.json())));

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

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl, JsonNode json) {
    }
}