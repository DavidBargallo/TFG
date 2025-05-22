package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;

import es.etg.dam.tfg.programa.JavaFXApp;
import es.etg.dam.tfg.programa.modelo.Usuario;
import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import es.etg.dam.tfg.programa.servicio.UsuarioVideojuegoServicio;
import es.etg.dam.tfg.programa.utils.AlertaUtils;
import es.etg.dam.tfg.programa.utils.FXMLSoporte;
import es.etg.dam.tfg.programa.utils.ImagenUtils;
import es.etg.dam.tfg.programa.utils.Mensajes;
import es.etg.dam.tfg.programa.utils.Paginador;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WishlistControlador {

    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final RawgApiServicio rawgApiServicio;
    private final ApplicationContext applicationContext;

    @FXML private VBox contenedorWishlist;
    @FXML private Label lblPagina;
    @FXML private Button btnAnterior, btnSiguiente;

    private List<UsuarioVideojuego> juegosWishlist;
    private Paginador<UsuarioVideojuego> paginador;

    @FXML
    public void initialize() {
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
            VBox tarjeta = new VBox(5);
            tarjeta.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
            Videojuego juego = uv.getVideojuego();

            ImageView img = new ImageView();
            img.setFitHeight(150);
            img.setFitWidth(100);
            ImagenUtils.cargarImagen(img, juego.getPortadaUrl());

            Label lblNombre = new Label(juego.getNombre());
            lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            VBox tiendasBox = new VBox(2);
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
                            Hyperlink enlace = new Hyperlink(nombreTienda);
                            enlace.setOnAction(e -> getHostServices().showDocument(tienda.get("url").asText()));
                            tiendasBox.getChildren().add(enlace);
                        }
                    }
                }
            } catch (Exception e) {
                tiendasBox.getChildren().add(new Label("No se pudo cargar tiendas."));
            }

            Button btnAgregar = new Button("Agregar a Biblioteca");
            btnAgregar.setOnAction(e -> agregarABiblioteca(uv));

            tarjeta.getChildren().addAll(img, lblNombre, new Label("Tiendas disponibles:"), tiendasBox, btnAgregar);
            contenedorWishlist.getChildren().add(tarjeta);
        }

        btnAnterior.setDisable(!paginador.puedeIrAnterior());
        btnSiguiente.setDisable(!paginador.puedeIrSiguiente());
    }

    private void agregarABiblioteca(UsuarioVideojuego uv) {
        uv.setEnWishlist(false);
        usuarioVideojuegoServicio.agregarVideojuegoAUsuario(uv);
        juegosWishlist.remove(uv);
        paginador = new Paginador<>(juegosWishlist, 5);
        mostrarPagina();
        AlertaUtils.mostrarAlerta("Juego agregado a tu biblioteca.");
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
        FXMLSoporte.abrirVentana(applicationContext, "/vista/Biblioteca.fxml", "Biblioteca", (Stage) contenedorWishlist.getScene().getWindow());
    }

    private javafx.application.HostServices getHostServices() {
    return JavaFXApp.getHostServicesInstance();
}

}

