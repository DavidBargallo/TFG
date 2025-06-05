package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.modelo.ids.UsuarioVideojuegoID;
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
    private final RawgApiServicio rawgApiServicio;
    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final GeneroServicio generoServicio;
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
    private Menu menuCuenta;

    @FXML
    public void initialize() {
        Sesion.nombreUsuario(menuCuenta);
        menuVolverBiblioteca.setOnAction(e -> volverABiblioteca());
        cargarPlataformas();
        cargarGeneros();

        comboOrdenApi.getItems().addAll(Mensajes.NINGUNO, Mensajes.FILTRO_BIBLIOTECA_RECIENTE, Mensajes.FILTRO_BIBLIOTECA_ANTIGUA);
        comboOrdenApi.getSelectionModel().selectFirst();

        btnBuscarApi.setOnAction(this::buscarJuegos);
    }

    private void cargarPlataformas() {
        try {
            JsonNode plataformas = rawgApiServicio.consumirApi(Mensajes.ENLACE_PLATAFORMAS).get(Mensajes.API_RESULTADOS);
            comboConsolaApi.getItems().clear();
            comboConsolaApi.getItems().add(Mensajes.TODAS);

            plataformas.forEach(p -> {
                String nombre = p.get(Mensajes.API_NOMBRE).asText();
                int id = p.get(Mensajes.API_ID).asInt();
                comboConsolaApi.getItems().add(nombre);
                mapaConsolas.put(nombre, id);
            });

            comboConsolaApi.getSelectionModel().selectFirst();
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta(Mensajes.ERROR_CARGAR_PLATAFORMAS);
        }
    }

    private void cargarGeneros() {
        try {
            JsonNode generos = rawgApiServicio.consumirApi(Mensajes.ENLACE_GENEROS).get(Mensajes.API_RESULTADOS);
            comboGeneroApi.getItems().clear();
            comboGeneroApi.getItems().add(Mensajes.TODOS);

            generos.forEach(g -> {
                String nombre = g.get(Mensajes.API_NOMBRE).asText();
                int id = g.get(Mensajes.API_ID).asInt();
                comboGeneroApi.getItems().add(nombre);
                mapaGeneros.put(nombre, id);
            });

            comboGeneroApi.getSelectionModel().selectFirst();
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta(Mensajes.ERROR_CARGAR_GENEROS);
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

        Integer generoId = Mensajes.TODOS.equals(generoSeleccionado) ? null : mapaGeneros.get(generoSeleccionado);
        Integer plataformaId = Mensajes.TODAS.equals(consolaSeleccionada) ? null : mapaConsolas.get(consolaSeleccionada);
        String ordenFinal = Mensajes.NINGUNO.equals(ordenSeleccionado) ? null : ordenSeleccionado;

        try {
            JsonNode respuesta = rawgApiServicio.buscarJuegos(nombre, generoId, plataformaId, ordenFinal,
                    paginaActualApi, JUEGOS_POR_PAGINA);

            JsonNode juegos = respuesta.get(Mensajes.API_RESULTADOS);
            contenedorResultadosApi.getChildren().clear();

            if (juegos != null && juegos.isArray() && juegos.size() > 0) {
                List<JsonNode> listaJuegos = new ArrayList<>();
                juegos.forEach(listaJuegos::add);

                listaJuegos.removeIf(juego -> {
                    if (!Mensajes.TODAS.equals(consolaSeleccionada) && !tienePlataforma(juego, consolaSeleccionada)) {
                        return true;
                    }
                    if (!Mensajes.TODOS.equals(generoSeleccionado) && !tieneGenero(juego, generoSeleccionado)) {
                        return true;
                    }
                    return false;
                });

                listaJuegos.sort(
                        Comparator
                                .comparing((JsonNode j) -> !j.get(Mensajes.API_NOMBRE).asText().toLowerCase().contains(nombre))

                                .thenComparing((JsonNode j1, JsonNode j2) -> {
                                    if (Mensajes.FILTRO_BIBLIOTECA_RECIENTE.equals(ordenSeleccionado)) {
                                        return extraerFecha(j2).compareTo(extraerFecha(j1));
                                    } else if (Mensajes.FILTRO_BIBLIOTECA_ANTIGUA.equals(ordenSeleccionado)) {
                                        return extraerFecha(j1).compareTo(extraerFecha(j2));
                                    }
                                    return 0;
                                }));

                if (listaJuegos.isEmpty()) {
                    contenedorResultadosApi.getChildren()
                            .add(new Label(Mensajes.SIN_RESULTADOS));
                } else {
                    listaJuegos.forEach(j -> contenedorResultadosApi.getChildren().add(crearFichaJuego(j)));
                }

            } else {
                contenedorResultadosApi.getChildren()
                        .add(new Label(Mensajes.SIN_RESULTADOS));
            }

            lblPaginaApi.setText(Mensajes.PAGINA + paginaActualApi);
            btnAnteriorApi.setDisable(paginaActualApi == 1);
            btnSiguienteApi.setDisable(!respuesta.has(Mensajes.API_SIGUIENTE) || respuesta.get(Mensajes.API_SIGUIENTE).isNull());

        } catch (IOException | InterruptedException e) {
            AlertaUtils.mostrarAlerta(Mensajes.ERROR_CARGAR_RESULTADOS + e.getMessage());
        }
    }

    private boolean tieneGenero(JsonNode juego, String generoSeleccionado) {
        if (generoSeleccionado == null || generoSeleccionado.equals(Mensajes.TODOS))
            return true;
        if (!juego.has(Mensajes.API_GENEROS))
            return false;

        for (JsonNode gen : juego.get(Mensajes.API_GENEROS)) {
            String nombre = gen.get(Mensajes.API_NOMBRE).asText();
            if (generoSeleccionado.equalsIgnoreCase(nombre))
                return true;
        }
        return false;
    }

    private boolean tienePlataforma(JsonNode juego, String consolaSeleccionada) {
        if (consolaSeleccionada == null || consolaSeleccionada.equals(Mensajes.TODAS))
            return false;
        if (!juego.has(Mensajes.API_PLATAFORMAS))
            return false;

        for (JsonNode plat : juego.get(Mensajes.API_PLATAFORMAS)) {
            String nombre = plat.get(Mensajes.API_PLATAFORMA).get(Mensajes.API_NOMBRE).asText();
            if (consolaSeleccionada.equalsIgnoreCase(nombre))
                return true;
        }
        return false;
    }

    private LocalDate extraerFecha(JsonNode juego) {
        if (juego.hasNonNull(Mensajes.API_LANZADO)) {
            try {
                return LocalDate.parse(juego.get(Mensajes.API_LANZADO).asText());
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
                Mensajes.TITULO_BIBLIOTECA,
                actual,
                BibliotecaControlador::inicializarBiblioteca);
    }

    private HBox crearFichaJuego(JsonNode juegoJson) {
        String nombreJuego = juegoJson.get(Mensajes.API_NOMBRE).asText();
        String imagenUrl = juegoJson.hasNonNull(Mensajes.API_IMAGEN)
                ? juegoJson.get(Mensajes.API_IMAGEN).asText()
                : null;
        LocalDate fecha = juegoJson.hasNonNull(Mensajes.API_LANZADO)
                ? LocalDate.parse(juegoJson.get(Mensajes.API_LANZADO).asText())
                : LocalDate.of(1900, 1, 1);

        Image imagen = crearImagen(imagenUrl);
        ImageView imageView = new ImageView(imagen);
        HBox hBox = new HBox(10);
        hBox.setStyle(Mensajes.ESTILO_TARJETA);
        hBox.getChildren().addAll(imageView, new Label(nombreJuego));

        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            Optional<Videojuego> existente = videojuegoServicio.obtenerConRelacionesPorNombreYFecha(nombreJuego,
                    fecha);

            Optional<UsuarioVideojuego> uvOpt = existente.flatMap(
                    v -> usuarioVideojuegoServicio.obtenerVideojuegoPorId(new UsuarioVideojuegoID(usuario.getId(), v.getId())));

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
                    : new Image(getClass().getResource(RutaFXML.PLACEHOLDER).toExternalForm(), 120, 80, true, true);
        } catch (Exception e) {
            return new Image(getClass().getResource(RutaFXML.PLACEHOLDER).toExternalForm(), 120, 80, true, true);
        }
    }

    private Button crearBotonAgregar(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp temp) {
        Button btn = new Button(Mensajes.AGREGAR_BIBLIOTECA);

        btn.setOnAction(e -> {
            try {
                Videojuego juego = (juegoExistente != null) ? juegoExistente : crearVideojuegoDesdeApi(temp);

                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), juego.getId());
                Optional<UsuarioVideojuego> relacionExistente = usuarioVideojuegoServicio.obtenerVideojuegoPorId(id);

                if (relacionExistente.isPresent()) {
                    UsuarioVideojuego relacion = relacionExistente.get();
                    if (!relacion.isEnWishlist()) {
                        AlertaUtils.mostrarAlerta(Mensajes.JUEGO_YA_EN_BIBLIOTECA);
                        return;
                    }

                    if (juego.getConsolas() == null || juego.getConsolas().isEmpty()) {
                        juego.setConsolas(
                                Set.of(FormularioJuegoUtils.seleccionarConsolaDesdeJson(temp.json(), consolaServicio)));
                    }
                    if (juego.getGeneros() == null || juego.getGeneros().isEmpty()) {
                        juego.setGeneros(obtenerGeneros(temp.json()));
                    }
                    FormularioJuegoUtils.completarCompaniaSiFalta(juegoExistente, temp.json(), companiaServicio,
                            rawgApiServicio);

                    boolean esFisico = FormularioJuegoUtils.preguntarFormatoJuego();
                    juego.setEsFisico(esFisico);
                    if (esFisico) {
                        juego.setUbicacion(FormularioJuegoUtils.obtenerUbicacionParaJuegoFisico(ubicacionServicio,
                                applicationContext));
                    }

                    videojuegoServicio.guardar(juego);

                    relacion.setEnWishlist(false);
                    usuarioVideojuegoServicio.guardar(relacion);

                    AlertaUtils.mostrarAlerta(Mensajes.JUEGO_MOVIDO_BIBLIOTECA);
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

                    usuarioVideojuegoServicio.guardar(nuevaRelacion);
                    AlertaUtils.mostrarAlerta(Mensajes.JUEGO_AGREGADO_BIBLIOTECA);
                }

                btn.setVisible(false);

            } catch (Exception ex) {
                AlertaUtils.mostrarAlerta(Mensajes.ERROR_AGREGAR_JUEGO + ex.getMessage());
            }
        });

        return btn;
    }

    private Button crearBotonWishlist(Usuario usuario, Videojuego juegoExistente, VideojuegoTemp temp) {
        Button btn = new Button(Mensajes.AGREGAR_WISHLIST);

        btn.setOnAction(e -> {
            try {
                Videojuego juego = (juegoExistente != null) ? juegoExistente : crearVideojuegoBasico(temp);
                UsuarioVideojuegoID id = new UsuarioVideojuegoID(usuario.getId(), juego.getId());

                Optional<UsuarioVideojuego> existente = usuarioVideojuegoServicio.obtenerVideojuegoPorId(id);

                UsuarioVideojuego relacion;
                if (existente.isPresent()) {
                    relacion = existente.get();
                    if (relacion.isEnWishlist()) {
                        AlertaUtils.mostrarAlerta(Mensajes.JUEGO_YA_EN_WISHLIST);
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

                usuarioVideojuegoServicio.guardar(relacion);

                AlertaUtils.mostrarAlerta(Mensajes.JUEGO_AGREGADO_WISHLIST);
                btn.setVisible(false);

            } catch (Exception ex) {
                AlertaUtils.mostrarAlerta(Mensajes.ERROR_AGREGAR_WISHLIST + ex.getMessage());
            }
        });

        return btn;
    }

    private Videojuego crearVideojuegoBasico(VideojuegoTemp temp) {
        Optional<Videojuego> existente = videojuegoServicio
                .obtenerPorNombreYFechaLanzamiento(temp.nombre(),
                        temp.fecha());
        if (existente.isPresent())
            return existente.get();

        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(false);

        v.setGeneros(obtenerGeneros(temp.json()));

        if (temp.json().has(Mensajes.API_DESARROLLADORES) && temp.json().get(Mensajes.API_DESARROLLADORES).isArray()) {
            for (JsonNode dev : temp.json().get(Mensajes.API_DESARROLLADORES)) {
                if (dev.hasNonNull(Mensajes.API_NOMBRE)) {
                    String nombre = dev.get(Mensajes.API_NOMBRE).asText();
                    Compania compania = companiaServicio.guardarSiNoExiste(nombre, Mensajes.VACIO, null, null);
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
        Optional<Videojuego> existente = videojuegoServicio
                .obtenerConRelacionesPorNombreYFecha(temp.nombre(), temp.fecha());
        if (existente.isPresent()) {
            return existente.get();
        }

        Videojuego v = new Videojuego();
        v.setNombre(temp.nombre());
        v.setFechaLanzamiento(temp.fecha());
        v.setPortadaUrl(temp.imagenUrl());
        v.setEsFisico(false);

        v.setGeneros(obtenerGeneros(temp.json()));

        try {
            JsonNode juegoCompleto = rawgApiServicio
                    .consumirApi(Mensajes.ENLACE_JUEGOS + temp.json().get(Mensajes.API_ID).asInt());

            if (juegoCompleto.has(Mensajes.API_DESARROLLADORES) && juegoCompleto.get(Mensajes.API_DESARROLLADORES).isArray()) {
                for (JsonNode dev : juegoCompleto.get(Mensajes.API_DESARROLLADORES)) {
                    if (dev.hasNonNull(Mensajes.API_NOMBRE)) {
                        String nombre = dev.get(Mensajes.API_NOMBRE).asText();
                        Compania compania = companiaServicio.guardarSiNoExiste(nombre, Mensajes.VACIO, null, null);
                        if (compania != null) {
                            v.setCompania(compania);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(Mensajes.ERROR_INFORMACION_DETALLADA + e.getMessage());
        }

        return videojuegoServicio.guardar(v);
    }

    private Set<Genero> obtenerGeneros(JsonNode json) {
        Set<Genero> generos = new HashSet<>();
        if (json.has(Mensajes.API_GENEROS)) {
            json.get(Mensajes.API_GENEROS).forEach(node -> {
                String nombre = node.get(Mensajes.API_NOMBRE).asText();
                generoServicio.obtenerPorNombre(nombre).ifPresent(generos::add);
            });
        }
        return generos;
    }

    private record VideojuegoTemp(String nombre, LocalDate fecha, String imagenUrl, JsonNode json) {
    }
}