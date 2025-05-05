package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.servicio.VideojuegoServicio;
import es.etg.dam.tfg.programa.servicio.ConsolaServicio;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BibliotecaControlador {

    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
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
    public void initialize() {
        comboOrden.getItems().addAll("Nombre A-Z", "Nombre Z-A", "Precio ascendente", "Precio descendente");
        comboOrden.getSelectionModel().selectFirst();

        comboConsola.getItems().addAll(consolaServicio.obtenerTodas());

        mostrarTodosLosJuegos();
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();
        BigDecimal precioMaximo;

        try {
            precioMaximo = txtPrecioMaximo.getText().isBlank() ? null : new BigDecimal(txtPrecioMaximo.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato de precio incorrecto.");
            return;
        }

        Consola consolaSeleccionada = comboConsola.getValue();
        List<Videojuego> filtrados = videojuegoServicio.obtenerTodos().stream()
                .filter(j -> nombre.isEmpty() || j.getNombre().toLowerCase().contains(nombre))
                .filter(j -> consolaSeleccionada == null || j.getConsolas().contains(consolaSeleccionada))
                .filter(j -> precioMaximo == null || (j.getPrecio() != null && j.getPrecio().compareTo(precioMaximo) <= 0))
                .sorted(obtenerComparador(orden))
                .collect(Collectors.toList());

        mostrarJuegos(filtrados);
    }

    private Comparator<Videojuego> obtenerComparador(String orden) {
        return switch (orden) {
            case "Nombre A-Z" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Nombre Z-A" -> Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
            case "Precio ascendente" -> Comparator.comparing(Videojuego::getPrecio, Comparator.nullsLast(Comparator.naturalOrder()));
            case "Precio descendente" -> Comparator.comparing(Videojuego::getPrecio, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Videojuego::getNombre);
        };
    }

    private void mostrarTodosLosJuegos() {
        mostrarJuegos(videojuegoServicio.obtenerTodos());
    }

    private void mostrarJuegos(List<Videojuego> juegos) {
        contenedorResultados.getChildren().clear();
        juegos.forEach(j -> contenedorResultados.getChildren().add(crearFichaJuego(j)));
    }

    private VBox crearFichaJuego(Videojuego juego) {
        VBox ficha = new VBox(5);
        ficha.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9; -fx-padding: 10;");

        ImageView portada = new ImageView();
        portada.setFitWidth(100);
        portada.setFitHeight(100);
        portada.setPreserveRatio(true);

        try {
            portada.setImage(new Image(juego.getPortadaUrl(), true));
        } catch (Exception e) {
            portada.setImage(new Image(getClass().getResourceAsStream("/placeholder.png")));
        }

        Label lblNombre = new Label("Nombre: " + juego.getNombre());
        Label lblConsolas = new Label("Consolas: " + obtenerNombres(juego.getConsolas()));
        Label lblGeneros = new Label("Géneros: " + obtenerNombresGeneros(juego.getGeneros()));
        Label lblEmpresa = new Label("Empresa: " + Optional.ofNullable(juego.getCompania()).map(c -> c.getNombre()).orElse("N/A"));

        ficha.getChildren().addAll(portada, lblNombre, lblConsolas, lblGeneros, lblEmpresa);
        return ficha;
    }

    private String obtenerNombres(Set<? extends Object> entidades) {
        return entidades.stream()
                .map(e -> {
                    try {
                        return (String) e.getClass().getMethod("getNombre").invoke(e);
                    } catch (Exception ex) {
                        return "";
                    }
                })
                .collect(Collectors.joining(", "));
    }

    private String obtenerNombresGeneros(Set<?> generos) {
        return obtenerNombres(generos);
    }

    @FXML
    private void cerrarSesion() {
        abrirPantalla("/vista/pantalla_inicio.fxml", "Iniciar sesión");
    }

    @FXML
    private void abrirPerfil() {
        abrirPantalla("/vista/Perfil.fxml", "Mi Perfil");
    }

    @FXML
    private void cambiarContrasena() {
        abrirPantalla("/vista/CambiarContrasena.fxml", "Cambiar contraseña");
    }

    @FXML
    private void abrirFormularioAgregarJuego() {
        abrirPantalla("/vista/pantalla_busqueda.fxml", "Buscar en RAWG API");
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
            mostrarAlerta("Error al abrir la pantalla: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}



