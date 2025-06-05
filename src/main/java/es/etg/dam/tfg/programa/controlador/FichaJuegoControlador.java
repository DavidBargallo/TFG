package es.etg.dam.tfg.programa.controlador;

import org.springframework.stereotype.Component;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.utils.ImagenUtils;
import es.etg.dam.tfg.programa.utils.Mensajes;
import es.etg.dam.tfg.programa.utils.TextoUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FichaJuegoControlador {

    @FXML private ImageView imgPortada;
    @FXML private Label lblNombre;
    @FXML private Label lblFecha;
    @FXML private Label lblEmpresa;
    @FXML private Label lblGeneros;
    @FXML private Label lblConsolas;
    @FXML private Label lblFisico;
    @FXML private Label lblUbicacion;

    private Stage stage;

    public void inicializarDatos(Videojuego juego) {
        if (juego == null) return;

        lblNombre.setText(juego.getNombre());
        lblFecha.setText(Mensajes.FECHA + juego.getFechaLanzamiento());
        lblEmpresa.setText(Mensajes.EMPRESA + 
            (juego.getCompania() != null ? juego.getCompania().getNombre() : Mensajes.VACIO));
        lblGeneros.setText(Mensajes.GENERO + TextoUtils.obtenerNombres(juego.getGeneros()));
        lblConsolas.setText(Mensajes.CONSOLA + TextoUtils.obtenerNombres(juego.getConsolas()));
        lblFisico.setText(Mensajes.FORMATO + (juego.isEsFisico() ? Mensajes.FORMATO_FISICO : Mensajes.FORMATO_DIGITAL));
        lblUbicacion.setText(Mensajes.UBICACION + (null != juego.getUbicacion() ? juego.getUbicacion().getLugar() + "," + juego.getUbicacion().getZona() : "N/A"));
        ImagenUtils.cargarImagen(imgPortada, juego.getPortadaUrl());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void cerrarVentana() {
        if (stage != null) {
            stage.close();
        } else {
            Stage current = (Stage) imgPortada.getScene().getWindow();
            current.close();
        }
    }
}

