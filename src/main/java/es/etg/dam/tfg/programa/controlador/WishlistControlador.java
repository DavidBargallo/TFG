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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WishlistControlador {

    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final RawgApiServicio rawgApiServicio;
    private final VideojuegoServicio videojuegoServicio;
    private final ConsolaServicio consolaServicio;
    private final UbicacionServicio ubicacionServicio;
    private final CompaniaServicio companiaServicio;
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
        lblPagina.setText(Mensajes.PAGINA + paginador.getPaginaActualNumero());

        for (UsuarioVideojuego uv : paginador.getPaginaActual()) {
            VBox tarjeta = new VBox(10);
            tarjeta.setStyle(Mensajes.ESTILO_TARJETA);
            Videojuego juego = uv.getVideojuego();

            ImageView img = new ImageView();
            img.setFitHeight(150);
            img.setFitWidth(100);
            ImagenUtils.cargarImagen(img, juego.getPortadaUrl());

            Label lblNombre = new Label(juego.getNombre());
            lblNombre.setStyle(Mensajes.ESTILO_NOMBRE_WISHLIST);

            VBox tiendasBox = new VBox(5);
            tiendasBox.getChildren().add(new Label(Mensajes.TIENDAS_DISPONIBLES));
            try {
                JsonNode infoJuego = rawgApiServicio.buscarJuegos(juego.getNombre(), null, null, null, 1, 1);
                JsonNode resultados = infoJuego.get(Mensajes.API_RESULTADOS);
                if (resultados != null && resultados.isArray() && resultados.size() > 0) {
                    String id = resultados.get(0).get(Mensajes.API_ID).asText();
                    JsonNode detalle = rawgApiServicio.obtenerJuegoPorId(id);
                    JsonNode tiendas = detalle.get(Mensajes.API_STORES);

                    if (tiendas != null && tiendas.isArray()) {
                        for (JsonNode tienda : tiendas) {
                            String nombreTienda = tienda.get(Mensajes.API_STORE).get(Mensajes.API_NOMBRE).asText();
                            String dominio = tienda.get(Mensajes.API_STORE).get(Mensajes.API_DOMINIO).asText();
                            String url = Mensajes.API_HTTPS + dominio;

                            Hyperlink enlace = new Hyperlink(nombreTienda);
                            enlace.setOnAction(e -> getHostServices().showDocument(url));
                            tiendasBox.getChildren().add(enlace);

                        }
                    }
                }
            } catch (Exception e) {
                tiendasBox.getChildren().add(new Label(Mensajes.ERROR_TIENDA));
            }

            Button btnAgregar = new Button(Mensajes.AGREGAR_BIBLIOTECA);
            btnAgregar.setOnAction(e -> agregarABiblioteca(juego, uv));

            tarjeta.getChildren().addAll(img, lblNombre, tiendasBox, btnAgregar);
            contenedorWishlist.getChildren().add(tarjeta);
        }

        btnAnterior.setDisable(!paginador.puedeIrAnterior());
        btnSiguiente.setDisable(!paginador.puedeIrSiguiente());
    }

    private void agregarABiblioteca(Videojuego juego, UsuarioVideojuego uv) {
        try {
            boolean esFisico = FormularioJuegoUtils.preguntarFormatoJuego();
            juego.setEsFisico(esFisico);
            if (esFisico) {
                Ubicacion ubicacion = FormularioJuegoUtils.obtenerUbicacionParaJuegoFisico(ubicacionServicio,
                        applicationContext);
                juego.setUbicacion(ubicacion);
            }

            if (juego.getConsolas() == null || juego.getConsolas().isEmpty()) {
                Consola consola = FormularioJuegoUtils.seleccionarConsolaDesdeVideojuego(juego, consolaServicio);
                juego.setConsolas(Set.of(consola));
            }

            videojuegoServicio.guardar(juego);

            uv.setEnWishlist(false);
            try {
                JsonNode respuesta = rawgApiServicio.buscarJuegos(juego.getNombre(), null, null, null, 1, 1);
                JsonNode resultados = respuesta.get(Mensajes.API_RESULTADOS);
                if (resultados != null && resultados.isArray() && resultados.size() > 0) {
                    String rawgId = resultados.get(0).get(Mensajes.API_ID).asText();
                    JsonNode detalle = rawgApiServicio.obtenerJuegoPorId(rawgId);

                    FormularioJuegoUtils.completarCompaniaSiFalta(juego, detalle, companiaServicio, rawgApiServicio);
                }
            } catch (Exception ex) {
                System.out.println(Mensajes.ERROR_COMPANIA + ex.getMessage());
            }

            usuarioVideojuegoServicio.agregarVideojuegoAUsuario(uv);
            juegosWishlist.remove(uv);
            paginador = new Paginador<>(juegosWishlist, 5);
            mostrarPagina();
            AlertaUtils.mostrarAlerta(Mensajes.JUEGO_AGREGADO_BIBLIOTECA);
        } catch (Exception e) {
            AlertaUtils.mostrarAlerta(Mensajes.ERROR_AGREGAR_JUEGO + e.getMessage());
        }
    }

    @FXML
    private void exportarWishlistAPDF() {
        if (juegosWishlist == null || juegosWishlist.isEmpty()) {
            AlertaUtils.mostrarAlerta(Mensajes.WISHLIST_VACIA);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Mensajes.GUARDAR_WISHLIST_PDF);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Mensajes.DESCRIPCION_PDF, Mensajes.EXTENSION_PDF));
        File archivo = fileChooser.showSaveDialog(null);

        if (archivo != null) {
            try {
                List<Videojuego> juegos = juegosWishlist.stream()
                        .map(UsuarioVideojuego::getVideojuego)
                        .toList();

                PDFUtils.exportarWishlist(juegos, Sesion.getUsuarioActual().getNombreUsuario(), archivo);
                AlertaUtils.mostrarAlerta(Mensajes.WISHLIST_EXPORTADA);
            } catch (IOException e) {
                FXMLSoporte.mostrarError(Mensajes.ERROR_EXPORTAR_WISHLIST + e.getMessage());
            }
        }
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
