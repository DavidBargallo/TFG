package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.JavaFXApp;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.servicio.*;
import es.etg.dam.tfg.programa.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WishlistControlador {

    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final RawgApiServicio rawgApiServicio;
    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
    private final UbicacionServicio ubicacionServicio;
    private final ApplicationContext applicationContext;

    @FXML
    private VBox contenedorWishlist;
    @FXML
    private Label lblPagina;
    @FXML
    private Button btnAnterior, btnSiguiente;
    @FXML
    private Menu menuCuenta;

    private List<UsuarioVideojuego> juegosWishlist;
    private Paginador<UsuarioVideojuego> paginador;

    @FXML
    public void initialize() {
        Sesion.nombreUsuario(menuCuenta);
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) {
            FXMLSoporte.mostrarError(Mensajes.USUARIO_NO_LOGUEADO);
            return;
        }

        juegosWishlist = usuarioVideojuegoServicio.obtenerVideojuegosEnWishlist(usuario.getId());
        paginador = new Paginador<>(juegosWishlist, 5);
        mostrarPagina();
    }

    private void mostrarPagina() {
        contenedorWishlist.getChildren().clear();
        lblPagina.setText("Página " + paginador.getPaginaActualNumero());

        for (UsuarioVideojuego uv : paginador.getPaginaActual()) {
            VBox tarjeta = new VBox(10);
            tarjeta.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
            Videojuego juego = uv.getVideojuego();

            ImageView img = new ImageView();
            img.setFitHeight(150);
            img.setFitWidth(100);
            ImagenUtils.cargarImagen(img, juego.getPortadaUrl());

            Label lblNombre = new Label(juego.getNombre());
            lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            VBox tiendasBox = new VBox(5);
            tiendasBox.getChildren().add(new Label("Tiendas disponibles:"));
            try {
                JsonNode infoJuego = rawgApiServicio.buscarJuegos(juego.getNombre(), null, null, null, 1, 1);
                JsonNode resultados = infoJuego.get("results");
                if (resultados != null && resultados.isArray() && resultados.size() > 0) {
                    String id = resultados.get(0).get("id").asText();
                    JsonNode detalle = rawgApiServicio.obtenerJuegoPorId(id);
                    JsonNode tiendas = detalle.get("stores");

                    if (tiendas != null && tiendas.isArray()) {
                        for (JsonNode tienda : tiendas) {
                            String nombreTienda = tienda.get("store").get("name").asText();
                            String dominio = tienda.get("store").get("domain").asText();
                            String url = "https://" + dominio;

                            Hyperlink enlace = new Hyperlink(nombreTienda);
                            enlace.setOnAction(e -> getHostServices().showDocument(url));
                            tiendasBox.getChildren().add(enlace);

                        }
                    }
                }
            } catch (Exception e) {
                tiendasBox.getChildren().add(new Label("No se pudo cargar tiendas."));
            }

            Button btnAgregar = new Button("Agregar a Biblioteca");
            btnAgregar.setOnAction(e -> agregarABiblioteca(juego, uv));

            tarjeta.getChildren().addAll(img, lblNombre, tiendasBox, btnAgregar);
            contenedorWishlist.getChildren().add(tarjeta);
        }

        btnAnterior.setDisable(!paginador.puedeIrAnterior());
        btnSiguiente.setDisable(!paginador.puedeIrSiguiente());
    }

    private void agregarABiblioteca(Videojuego juego, UsuarioVideojuego uv) {
        try {
            boolean esFisico = preguntarFormatoJuego();
            juego.setEsFisico(esFisico);
            if (esFisico) {
                Ubicacion ubicacion = obtenerUbicacionParaJuegoFisico();
                juego.setUbicacion(ubicacion);
            }

            if (juego.getConsolas() == null || juego.getConsolas().isEmpty()) {
                Consola consola = seleccionarConsolaDesdeJuego(juego);
                juego.setConsolas(Set.of(consola));
            }

            videojuegoServicio.guardar(juego);

            uv.setEnWishlist(false);
            usuarioVideojuegoServicio.agregarVideojuegoAUsuario(uv);
            juegosWishlist.remove(uv);
            paginador = new Paginador<>(juegosWishlist, 5);
            mostrarPagina();
            AlertaUtils.mostrarAlerta("Juego agregado a tu biblioteca.");
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta("Error al agregar el juego: " + e.getMessage());
        }
    }

    private Consola seleccionarConsolaDesdeJuego(Videojuego juego) {
        List<Consola> disponibles = consolaServicio.obtenerTodas();
        if (disponibles.isEmpty()) {
            throw new RuntimeException("No hay consolas disponibles.");
        }

        List<String> nombres = disponibles.stream().map(Consola::getNombre).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Seleccionar consola");
        dialog.setHeaderText("Selecciona la consola en la que tienes este juego:");
        dialog.setContentText("Consola:");

        return dialog.showAndWait()
                .flatMap(nombre -> disponibles.stream().filter(c -> c.getNombre().equals(nombre)).findFirst())
                .orElseThrow(() -> new RuntimeException("Consola no seleccionada."));
    }

    private Ubicacion obtenerUbicacionParaJuegoFisico() {
        List<Ubicacion> ubicaciones = ubicacionServicio.obtenerTodas();

        if (!ubicaciones.isEmpty()) {
            ChoiceDialog<Ubicacion> dialog = new ChoiceDialog<>(ubicaciones.get(0), ubicaciones);
            dialog.setTitle("Seleccionar ubicación");
            dialog.setHeaderText("Selecciona una ubicación existente o cancela para crear una nueva:");
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

        throw new RuntimeException("Ubicación no establecida.");
    }

    private boolean preguntarFormatoJuego() {
        List<String> opciones = List.of("Físico", "Digital");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Físico", opciones);
        dialog.setTitle("Formato del juego");
        dialog.setHeaderText("¿Cómo tienes este juego?");
        dialog.setContentText("Formato:");
        return dialog.showAndWait().orElseThrow(() -> new RuntimeException("Selección cancelada.")).equals("Físico");
    }

    public void paginaAnterior() {
        paginador.irAnterior();
        mostrarPagina();
    }

    public void paginaSiguiente() {
        paginador.irSiguiente();
        mostrarPagina();
    }

    public void volverABiblioteca() {
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.BIBLIOTECA,
                "Biblioteca", (Stage) contenedorWishlist.getScene().getWindow());
    }

    private javafx.application.HostServices getHostServices() {
        return JavaFXApp.getHostServicesInstance();
    }
}
