package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;
import es.etg.dam.tfg.programa.servicio.ConsolaServicio;
import es.etg.dam.tfg.programa.servicio.GeneroServicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BibliotecaControlador {

    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
    private final GeneroServicio generoServicio;
    private final ApplicationContext applicationContext;

    @FXML
    private VBox contenedorResultados;
    @FXML
    private ComboBox<Consola> comboConsola;
    @FXML
    private ComboBox<String> comboOrden;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecioMaximo;
    @FXML
    private TextField txtNombreJuego;
    @FXML
    private DatePicker dateLanzamientoJuego;
    @FXML
    private TextField txtPortadaUrlJuego;
    @FXML
    private ComboBox<Consola> comboConsolasJuego;
    @FXML
    private ComboBox<String> comboGenerosJuego;
    @FXML
    private TextField txtCompaniaNombre;
    @FXML
    private VBox contenedorFormularioJuego;
    private boolean formularioVisible = false;

    @FXML
    public void initialize() {
        inicializarCombos();
        mostrarJuegos();
    }

    private void inicializarCombos() {
        comboOrden.getItems().addAll("Nombre A-Z", "Nombre Z-A", "Precio ascendente", "Precio descendente");
        comboOrden.getSelectionModel().selectFirst();
        comboConsola.getItems().addAll(consolaServicio.obtenerTodas());
        comboConsolasJuego.getItems().addAll(consolaServicio.obtenerTodas());
        List<String> nombresGeneros = generoServicio.obtenerTodos().stream().map(Genero::getNombre).collect(Collectors.toList());
        comboGenerosJuego.setItems(FXCollections.observableArrayList(nombresGeneros));
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();
        BigDecimal precioMaximo = obtenerPrecioMaximo();
        Consola consolaSeleccionada = comboConsola.getValue();

        List<Videojuego> filtrados = videojuegoServicio.obtenerTodos().stream()
                .filter(j -> nombre.isEmpty() || j.getNombre().toLowerCase().contains(nombre))
                .filter(j -> consolaSeleccionada == null || j.getConsolas().contains(consolaSeleccionada))
                .sorted(obtenerComparador(orden))
                .collect(Collectors.toList());

        mostrarJuegos(filtrados);
    }

    private BigDecimal obtenerPrecioMaximo() {
        try {
            return txtPrecioMaximo.getText().isBlank() ? null : new BigDecimal(txtPrecioMaximo.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato de precio incorrecto.");
            return null;
        }
    }

    private Comparator<Videojuego> obtenerComparador(String orden) {
        return switch (orden) {
            case "Nombre A-Z" ->
                    Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Nombre Z-A" ->
                    Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
            default -> Comparator.comparing(Videojuego::getNombre);
        };
    }

    private void mostrarJuegos() {
        List<Videojuego> juegos = videojuegoServicio.obtenerTodos();
        mostrarJuegos(juegos);
    }

    private void mostrarJuegos(List<Videojuego> juegos) {
        contenedorResultados.getChildren().clear();
        for (Videojuego juego : juegos) {
            contenedorResultados.getChildren().add(crearFichaJuego(juego));
        }
    }

    private VBox crearFichaJuego(Videojuego juego) {
        VBox ficha = new VBox(5);
        ficha.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9; -fx-padding: 10;");

        ImageView portada = new ImageView();
        portada.setFitWidth(100);
        portada.setFitHeight(100);
        portada.setPreserveRatio(true);
        cargarImagen(portada, juego.getPortadaUrl());

        Label lblNombre = new Label("Nombre: " + juego.getNombre());
        Label lblConsolas = new Label("Consolas: " + obtenerNombres(juego.getConsolas()));
        Label lblGeneros = new Label("Géneros: " + obtenerNombres(juego.getGeneros()));
        Label lblEmpresa = new Label("Empresa: " + Optional.ofNullable(juego.getCompania()).map(c -> c.getNombre()).orElse("N/A"));

        ficha.getChildren().addAll(portada, lblNombre, lblConsolas, lblGeneros, lblEmpresa);
        ficha.setOnMouseClicked(event -> mostrarDetallesJuego(juego));
        return ficha;
    }

    private void cargarImagen(ImageView imageView, String url) {
        try {
            imageView.setImage(new Image(url, true));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/placeholder.png")));
        }
    }

    private String obtenerNombres(Set<?> entidades) {
        return entidades.stream()
                .map(this::obtenerNombreDeEntidad)
                .collect(Collectors.joining(", "));
    }

    private String obtenerNombreDeEntidad(Object entidad) {
        try {
            return (String) entidad.getClass().getMethod("getNombre").invoke(entidad);
        } catch (Exception e) {
            return "";
        }
    }


    @FXML
    private void abrirFormularioAgregarJuego() {
       abrirPantalla("/vista/pantalla_busqueda.fxml", "Agregar juegos");
    }

    private void abrirPantalla(String ruta, String titulo) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                mostrarAlerta("No se pudo encontrar el recurso FXML: " + ruta);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean); 
            Parent root = loader.load();
            Stage stage = (Stage) contenedorResultados.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error al abrir la pantalla.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void guardarJuego() {
        String nombre = txtNombreJuego.getText();
        LocalDate fechaLanzamiento = dateLanzamientoJuego.getValue();
        String portadaUrl = txtPortadaUrlJuego.getText();
        Consola consolaSeleccionada = comboConsolasJuego.getValue();
        String nombreGenero = comboGenerosJuego.getValue();
        String nombreCompania = txtCompaniaNombre.getText();

        if (nombre == null || nombre.trim().isEmpty() || fechaLanzamiento == null) {
            mostrarAlerta("Nombre y fecha de lanzamiento son obligatorios.");
            return;
        }

        Videojuego nuevoVideojuego = new Videojuego();
        nuevoVideojuego.setNombre(nombre);
        nuevoVideojuego.setFechaLanzamiento(fechaLanzamiento);
        nuevoVideojuego.setPortadaUrl(portadaUrl);

        // Manejo de la Consola
        if (consolaSeleccionada != null) {
            Set<Consola> consolas = new HashSet<>();
            consolas.add(consolaSeleccionada);
            nuevoVideojuego.setConsolas(consolas);
        } else {
            mostrarAlerta("Debes seleccionar al menos una consola.");
            return;
        }

        // Manejo del Género
        if (nombreGenero != null && !nombreGenero.isEmpty()) {
            Genero genero = generoServicio.obtenerPorNombre(nombreGenero).orElse(null);
            if (genero == null) {
                mostrarAlerta("El género seleccionado no existe.");
                return;
            }
            Set<Genero> generos = new HashSet<>();
            generos.add(genero);
            nuevoVideojuego.setGeneros(generos);
        } else {
            mostrarAlerta("Debes seleccionar al menos un género.");
            return;
        }
        //TODO: Manejo de la Compañia.
        if (nombreCompania != null && !nombreCompania.isEmpty()) {
        }

        Videojuego juegoGuardado = videojuegoServicio.guardar(nuevoVideojuego);
        if (juegoGuardado != null) {
            mostrarAlerta("Juego guardado correctamente.");
            mostrarJuegos();
            limpiarFormularioAgregarJuego();
            abrirFormularioAgregarJuego();
        } else {
            mostrarAlerta("Error al guardar el juego.");
        }
    }

    private void limpiarFormularioAgregarJuego() {
        txtNombreJuego.clear();
        dateLanzamientoJuego.setValue(null);
        txtPortadaUrlJuego.clear();
        comboConsolasJuego.setValue(null);
        comboGenerosJuego.setValue(null);
        txtCompaniaNombre.clear();
    }

    private void mostrarDetallesJuego(Videojuego juego) {
        String detalles = "Nombre: " + juego.getNombre() + "\n" +
                "Fecha de Lanzamiento: " + juego.getFechaLanzamiento() + "\n" +
                "Portada URL: " + juego.getPortadaUrl() + "\n" +
                "Consolas: " + obtenerNombres(juego.getConsolas()) + "\n" +
                "Géneros: " + obtenerNombres(juego.getGeneros());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Juego");
        alert.setHeaderText(null);
        alert.setContentText(detalles);
        alert.showAndWait();
    }
}





