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
            Sesion.nombreUsuario(menuCuenta);
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
        comboOrden.getItems().addAll(Mensajes.FILTRO_BIBLIOTECA_A_Z, Mensajes.FILTRO_BIBLIOTECA_Z_A, Mensajes.FILTRO_BIBLIOTECA_RECIENTE, Mensajes.FILTRO_BIBLIOTECA_ANTIGUA);
        comboOrden.getSelectionModel().selectFirst();

        List<String> nombresConsolas = consolaServicio.obtenerTodas().stream()
                .map(Consola::getNombre)
                .collect(Collectors.toList());

        List<String> nombresGeneros = generoServicio.obtenerTodos().stream()
                .map(Genero::getNombre)
                .collect(Collectors.toList());

        ComboUtils.cargarComboConNombres(comboConsola, Mensajes.TODAS, nombresConsolas);
        ComboUtils.cargarComboConNombres(comboGenero, Mensajes.TODOS, nombresGeneros);
    }

    @FXML
    private void aplicarFiltros() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        String orden = comboOrden.getValue();

        String genero = comboGenero.getValue();
        String nombreConsola = comboConsola.getValue();

        genero = Mensajes.TODOS.equals(genero) ? null : genero;
        nombreConsola = Mensajes.TODAS.equals(nombreConsola) ? null : nombreConsola;

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
                .filter(uv -> !uv.isEnWishlist())
                .map(UsuarioVideojuego::getVideojuego)
                .toList();
    }

    private void mostrarJuegos() {
        List<Videojuego> juegos = cargarJuegosUsuario();
        if (juegos.isEmpty()) {
            contenedorResultados.getChildren().clear();
            contenedorResultados.getChildren().add(new Label(Mensajes.SIN_RESULTADOS));
            return;
        }
        paginador = new Paginador<>(juegos, 10);
        mostrarPaginaActual();
    }

    private void mostrarPaginaActual() {
        contenedorResultados.getChildren().clear();
        for (Videojuego juego : paginador.getPaginaActual()) {
            contenedorResultados.getChildren().add(crearFichaJuego(juego));
        }

        lblPagina.setText(Mensajes.PAGINA + paginador.getPaginaActualNumero() + Mensajes.DE + paginador.getTotalPaginas());
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
        ficha.setStyle(Mensajes.ESTILO_FICHA);

        ImageView portada = new ImageView();
        portada.setFitWidth(100);
        portada.setFitHeight(100);
        portada.setPreserveRatio(true);
        ImagenUtils.cargarImagen(portada, juego.getPortadaUrl());

        Label lblNombre = new Label(Mensajes.NOMBRE + juego.getNombre());
        Label lblConsolas = new Label(Mensajes.CONSOLA + TextoUtils.obtenerNombres(juego.getConsolas()));
        Label lblGeneros = new Label(Mensajes.GENERO + TextoUtils.obtenerNombres(juego.getGeneros()));
        Label lblEmpresa = new Label(Mensajes.EMPRESA +
                Optional.ofNullable(juego.getCompania()).map(c -> c.getNombre()).orElse(Mensajes.VACIO));

        Button btnEliminar = new Button(Mensajes.ELIMINAR);
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
        FXMLSoporte.abrirVentana(applicationContext, /*"/vista/pantalla_busqueda.fxml"*/RutaFXML.BUSQUEDA, Mensajes.TITULO_AGREGAR_JUEGOS,
                (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    private void abrirEstadisticas() {
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.ESTADISTICAS, Mensajes.TITULO_ESTADISTICAS,
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
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.LOGIN, Mensajes.TITULO_INICIO_SESION,
                (Stage) contenedorResultados.getScene().getWindow());
    }

    @FXML
    private void abrirWishlist() {
        FXMLSoporte.abrirVentana(applicationContext, RutaFXML.WISHLIST, Mensajes.TITULO_WISHLIST,
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
            FXMLSoporte.mostrarError(Mensajes.BIBLIOTECA_VACIA);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Mensajes.GUARDAR_BIBLIOTECA_PDF);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(/*Archivos PDF (*.pdf)"*/Mensajes.DESCRIPCION_PDF, Mensajes.EXTENSION_PDF/*"*.pdf"*/));
        File archivoGuardar = fileChooser.showSaveDialog(contenedorResultados.getScene().getWindow());

        if (archivoGuardar != null) {
            try {
                PDFUtils.exportarBiblioteca(juegosUsuario, Sesion.getUsuarioActual().getNombreUsuario(),
                        archivoGuardar);
            } catch (Exception e) {
                e.printStackTrace();
                FXMLSoporte.mostrarError(Mensajes.ERROR_EXPORTAR_PDF);
            }
        }
    }
}