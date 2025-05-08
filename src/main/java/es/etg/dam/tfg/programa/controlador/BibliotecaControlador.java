package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Consola;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BibliotecaControlador {

    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
    private final GeneroServicio generoServicio;
    private final ApplicationContext applicationContext;

    @FXML private VBox contenedorResultados;
    @FXML private ComboBox<Consola> comboConsola;
    @FXML private ComboBox<String> comboGenero;
    @FXML private ComboBox<String> comboOrden;
    @FXML private TextField txtNombre;

    public void initialize() {
        inicializarCombos();
        mostrarJuegos();
    }

    @FXML
    private void inicializarCombos() {
        comboOrden.getItems().addAll(
                "Nombre A-Z",
                "Nombre Z-A",
                "Fecha más reciente",
                "Fecha más antigua"
        );
        comboOrden.getSelectionModel().selectFirst();

        comboConsola.getItems().addAll(consolaServicio.obtenerTodas());

        comboGenero.getItems().add("Todos");
        generoServicio.obtenerTodos().stream()
                .map(g -> g.getNombre())
                .sorted()
                .forEach(comboGenero.getItems()::add);
        comboGenero.getSelectionModel().selectFirst();
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();
        Consola consolaSeleccionada = comboConsola.getValue();
        String generoSeleccionado = comboGenero.getValue();

        List<Videojuego> filtrados = videojuegoServicio.obtenerTodos().stream()
                .filter(j -> nombre.isEmpty() || j.getNombre().toLowerCase().contains(nombre))
                .filter(j -> consolaSeleccionada == null || j.getConsolas().contains(consolaSeleccionada))
                .filter(j -> generoSeleccionado == null || generoSeleccionado.equals("Todos") ||
                        j.getGeneros().stream().anyMatch(g -> g.getNombre().equalsIgnoreCase(generoSeleccionado)))
                .sorted(obtenerComparador(orden))
                .collect(Collectors.toList());

        mostrarJuegos(filtrados);
    }

    private Comparator<Videojuego> obtenerComparador(String orden) {
        return switch (orden) {
            case "Nombre A-Z" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Nombre Z-A" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
            case "Fecha más reciente" -> Comparator.comparing(Videojuego::getFechaLanzamiento).reversed();
            case "Fecha más antigua" -> Comparator.comparing(Videojuego::getFechaLanzamiento);
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

