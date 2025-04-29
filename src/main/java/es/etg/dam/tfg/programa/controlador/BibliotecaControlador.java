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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class BibliotecaControlador {

    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
    private final ApplicationContext applicationContext; 

    @FXML
    private VBox vbox_ficha_juego;

    @FXML
    private ComboBox<Consola> comboConsola;

    @FXML
    private ComboBox<String> comboOrden;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecioMax;

    @FXML
    private MenuItem men_item_cerr_ses;

    @FXML
    private MenuItem men_item_perfil;

    @FXML
    private MenuItem men_item_cam_contr;

    @FXML
    public void initialize() {
        cargarConsolas();
        configurarOrden();
        mostrarTodosLosJuegos();
        configurarEventosMenu();
    }

    private void cargarConsolas() {
        List<Consola> consolas = consolaServicio.obtenerTodas();
        comboConsola.getItems().addAll(consolas);
    }

    private void configurarOrden() {
        comboOrden.getItems().addAll("Nombre A-Z", "Nombre Z-A", "Precio ascendente", "Precio descendente");
        comboOrden.getSelectionModel().selectFirst();
    }

    private void configurarEventosMenu() {
        men_item_cerr_ses.setOnAction(e -> volverALogin());
        men_item_perfil.setOnAction(e -> abrirPantalla("/vista/Perfil.fxml", "Mi Perfil")); 
        men_item_cam_contr.setOnAction(e -> abrirPantalla("/vista/CambiarContrasena.fxml", "Cambiar contraseña")); 
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();
        final BigDecimal precioMax;
        try {
            if (!txtPrecioMax.getText().isBlank()) {
                precioMax = new BigDecimal(txtPrecioMax.getText());
            } else {
                precioMax = null;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato de precio incorrecto.");
            return;
        }


        Consola consolaSeleccionada = comboConsola.getValue();
        List<Videojuego> juegos = videojuegoServicio.obtenerTodos();

        List<Videojuego> filtrados = juegos.stream()
                .filter(j -> nombre.isEmpty() || j.getNombre().toLowerCase().contains(nombre))
                .filter(j -> consolaSeleccionada == null || j.getConsolas().contains(consolaSeleccionada))
                .filter(j -> precioMax == null || (j.getPrecio() != null && j.getPrecio().compareTo(precioMax) <= 0))
                .sorted(obtenerComparador(orden))
                .collect(Collectors.toList());

        mostrarJuegos(filtrados);
    }

    private Comparator<Videojuego> obtenerComparador(String orden) {
        return switch (orden) {
            case "Nombre A-Z" ->
                    Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Nombre Z-A" ->
                    Comparator.comparing(Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
            case "Precio ascendente" ->
                    Comparator.comparing(Videojuego::getPrecio, Comparator.nullsLast(Comparator.naturalOrder()));
            case "Precio descendente" ->
                    Comparator.comparing(Videojuego::getPrecio, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Videojuego::getNombre);
        };
    }

    private void mostrarTodosLosJuegos() {
        List<Videojuego> juegos = videojuegoServicio.obtenerTodos();
        mostrarJuegos(juegos);
    }

    private void mostrarJuegos(List<Videojuego> juegos) {
        vbox_ficha_juego.getChildren().clear();

        for (Videojuego juego : juegos) {
            VBox ficha = crearFichaJuego(juego);
            vbox_ficha_juego.getChildren().add(ficha);
        }
    }

    private VBox crearFichaJuego(Videojuego juego) {
        VBox ficha = new VBox(5);
        ficha.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9; -fx-padding: 10;");

        ImageView portada = new ImageView();
        portada.setFitWidth(100);
        portada.setFitHeight(100);
        portada.setPreserveRatio(true);
        if (juego.getPortadaUrl() != null) {
            try {
                portada.setImage(new Image(juego.getPortadaUrl(), true));
            } catch (Exception e) {
                portada.setImage(new Image(getClass().getResourceAsStream("/placeholder.png"))); 
            }
        }

        Label lblNombre = new Label("Nombre: " + juego.getNombre());
        Label lblConsola = new Label("Consolas: " + obtenerNombreConsolas(juego.getConsolas()));
        Label lblGenero = new Label("Géneros: " + obtenerNombreGeneros(juego.getGeneros()));
        Label lblEmpresa = new Label("Empresa: " + (juego.getCompania() != null ? juego.getCompania().getNombre() : "N/A"));

        ficha.getChildren().addAll(portada, lblNombre, lblConsola, lblGenero, lblEmpresa);
        return ficha;
    }

    private String obtenerNombreConsolas(Set<Consola> consolas) {
        return consolas.stream().map(Consola::getNombre).collect(Collectors.joining(", "));
    }

    private String obtenerNombreGeneros(Set<?> generos) {
        return generos.stream().map(g -> {
            try {
                return (String) g.getClass().getMethod("getNombre").invoke(g);
            } catch (Exception e) {
                return "";
            }
        }).collect(Collectors.joining(", "));
    }

    private void volverALogin() {
        abrirPantalla("/vista/pantalla_inicio.fxml", "Iniciar sesión"); 
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
            Stage stage = (Stage) vbox_ficha_juego.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error al abrir la pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}



