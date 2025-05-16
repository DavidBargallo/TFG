package es.etg.dam.tfg.programa.controlador;

import org.springframework.stereotype.Component;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import es.etg.dam.tfg.programa.utils.ImagenUtils;
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

    private Stage stage;

    public void inicializarDatos(Videojuego juego) {
        if (juego == null) return;

        lblNombre.setText(juego.getNombre());
        lblFecha.setText("Fecha: " + juego.getFechaLanzamiento());
        lblEmpresa.setText("Empresa: " + 
            (juego.getCompania() != null ? juego.getCompania().getNombre() : "N/A"));
        lblGeneros.setText("Géneros: " + TextoUtils.obtenerNombres(juego.getGeneros()));
        lblConsolas.setText("Consolas: " + TextoUtils.obtenerNombres(juego.getConsolas()));
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

