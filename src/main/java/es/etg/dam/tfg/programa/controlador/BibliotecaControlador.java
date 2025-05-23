package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.modelo.Consola;
import es.etg.dam.tfg.programa.modelo.Genero;
import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.servicio.ConsolaServicio;
import es.etg.dam.tfg.programa.servicio.GeneroServicio;
import es.etg.dam.tfg.programa.servicio.UsuarioVideojuegoServicio;
import es.etg.dam.tfg.programa.utils.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BibliotecaControlador {

    private final ConsolaServicio consolaServicio;
    private final GeneroServicio generoServicio;
    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final ApplicationContext applicationContext;


    private Paginador<Videojuego> paginador;

    @FXML
    private VBox contenedorResultados;
    @FXML
    private ComboBox<String> comboConsola;
    @FXML
    private ComboBox<String> comboGenero;
    @FXML
    private ComboBox<String> comboOrden;
    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnAnterior, btnSiguiente;
    @FXML
    private Label lblPagina;
    @FXML
    private Menu menuCuenta;

    @FXML
    public void initialize() {
        if (Sesion.haySesionActiva()) {
            menuCuenta.setText(Sesion.getUsuarioActual().getNombreUsuario());
            inicializarCombos();
            mostrarJuegos();
        } else {
            FXMLSoporte.mostrarError(Mensajes.USUARIO_NO_LOGUEADO);
        }
    }

    public void inicializarBiblioteca() {

        if (Sesion.haySesionActiva()) {
            menuCuenta.setText(Sesion.getUsuarioActual().getNombreUsuario());
        } else {
            return;
        }

        inicializarCombos();
        mostrarJuegos();
    }

    private void inicializarCombos() {
        comboOrden.getItems().clear();
        comboOrden.getItems().addAll("Nombre A-Z", "Nombre Z-A", "Fecha más reciente", "Fecha más antigua");
        comboOrden.getSelectionModel().selectFirst();

        List<String> nombresConsolas = consolaServicio.obtenerTodas().stream()
                .map(Consola::getNombre)
                .collect(Collectors.toList());

        List<String> nombresGeneros = generoServicio.obtenerTodos().stream()
                .map(Genero::getNombre)
                .collect(Collectors.toList());

        ComboUtils.cargarComboConNombres(comboConsola, "Todas", nombresConsolas);
        ComboUtils.cargarComboConNombres(comboGenero, "Todos", nombresGeneros);
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();

        String genero = comboGenero.getValue();
        String nombreConsola = comboConsola.getValue();

        genero = "Todos".equals(genero) ? null : genero;
        nombreConsola = "Todas".equals(nombreConsola) ? null : nombreConsola;

        List<Videojuego> juegosFiltrados = FiltroVideojuego.filtrarYOrdenar(
                cargarJuegosUsuario(), nombre, nombreConsola, genero, orden);

        paginador = new Paginador<>(juegosFiltrados, 10);
        mostrarPaginaActual();
    }

    private List<Videojuego> cargarJuegosUsuario() {
    var usuario = Sesion.getUsuarioActual();
    if (usuario == null)
        return Collections.emptyList();

    return usuarioVideojuegoServicio
            .obtenerVideojuegosPorUsuario(usuario.getId())
            .stream()
            .filter(uv -> !uv.isEnWishlist()) // Línea de código para la wishlist.
            .map(UsuarioVideojuego::getVideojuego)
            .toList();
}


    private void mostrarJuegos() {
        List<Videojuego> juegos = cargarJuegosUsuario();
        paginador = new Paginador<>(juegos, 10);
        mostrarPaginaActual();
    }

    private void mostrarPaginaActual() {
        contenedorResultados.getChildren().clear();
        for (Videojuego juego : paginador.getPaginaActual()) {
            contenedorResultados.getChildren().add(crearFichaJuego(juego));
        }

        lblPagina.setText("Página " + paginador.getPaginaActualNumero() + " de " + paginador.getTotalPaginas());
        btnAnterior.setDisable(!paginador.puedeIrAnterior());
        btnSiguiente.setDisable(!paginador.puedeIrSiguiente());
    }

    @FXML
    private void paginaAnterior() {
        paginador.irAnterior();
        mostrarPaginaActual();
    }

    @FXML
    private void paginaSiguiente() {
        paginador.irSiguiente();
        mostrarPaginaActual();
    }

    private VBox crearFichaJuego(Videojuego juego) {
        VBox ficha = new VBox(5);
        ficha.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9; -fx-padding: 10;");

        ImageView portada = new ImageView();
        portada.setFitWidth(100);
        portada.setFitHeight(100);
        portada.setPreserveRatio(true);
        ImagenUtils.cargarImagen(portada, juego.getPortadaUrl());

        Label lblNombre = new Label("Nombre: " + juego.getNombre());
        Label lblConsolas = new Label("Consolas: " + TextoUtils.obtenerNombres(juego.getConsolas()));
        Label lblGeneros = new Label("Géneros: " + TextoUtils.obtenerNombres(juego.getGeneros()));
        Label lblEmpresa = new Label("Empresa: " +
                Optional.ofNullable(juego.getCompania()).map(c -> c.getNombre()).orElse("N/A"));

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setOnAction(e -> eliminarJuego(juego));

        ficha.getChildren().addAll(portada, lblNombre, lblConsolas, lblGeneros, lblEmpresa, btnEliminar);
        ficha.setOnMouseClicked(event -> abrirFichaJuego(juego));
        return ficha;
    }

    private void eliminarJuego(Videojuego juego) {
        var usuario = Sesion.getUsuarioActual();
        if (usuario == null)
            return;

        if (AlertaUtils.confirmar(Mensajes.CONFIRMAR_ELIMINACION)) {
            usuarioVideojuegoServicio.eliminarRelacionUsuarioVideojuego(usuario.getId(), juego.getId());
            mostrarJuegos();
        }
    }

    @FXML
    private void abrirFormularioAgregarJuego() {
        FXMLSoporte.abrirVentana(applicationContext, "/vista/pantalla_busqueda.fxml", "Agregar juegos",
                (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    private void abrirEstadisticas() {
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.ESTADISTICAS, "Estadísticas",
                (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    private void abrirFichaJuego(Videojuego juego) {
        FXMLSoporte.abrirVentanaSecundaria(
                applicationContext,
                RutaFXML.FICHA_JUEGO,
                juego.getNombre(),
                (FichaJuegoControlador c) -> c.inicializarDatos(juego));
    }

    @FXML
    private void cerrarSesion() {
        Sesion.cerrarSesion();
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.LOGIN, "Iniciar sesión",
                (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    private void abrirWishlist() {
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.WISHLIST, "Wishlist", 
            (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    public void exportarBibliotecaAPDF() {
        if (!Sesion.haySesionActiva()) {
            FXMLSoporte.mostrarError(Mensajes.USUARIO_NO_LOGUEADO);
            return;
        }

        List<Videojuego> juegosUsuario = cargarJuegosUsuario();
        if (juegosUsuario.isEmpty()) {
            FXMLSoporte.mostrarError("La biblioteca está vacía.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Biblioteca como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        File archivoGuardar = fileChooser.showSaveDialog(contenedorResultados.getScene().getWindow());

        if (archivoGuardar != null) {
            try {
                PDFUtils.exportarBiblioteca(juegosUsuario, Sesion.getUsuarioActual().getNombreUsuario(),
                        archivoGuardar);
            } catch (Exception e) {
                FXMLSoporte.mostrarError("Error al exportar el PDF.");
            }
        }
    }
}