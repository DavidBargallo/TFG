package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.Ubicacion;
import es.etg.dam.tfg.programa.servicio.UbicacionServicio;
import es.etg.dam.tfg.programa.utils.Mensajes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class NuevaUbicacionControlador {

    private final UbicacionServicio ubicacionServicio;

    private Consumer<Ubicacion> onUbicacionGuardada;

    @FXML private TextField txtLugar;
    @FXML private TextField txtZona;
    @FXML private TextField txtIndicaciones;
    @FXML private Label lblError;

    public void setOnUbicacionGuardada(Consumer<Ubicacion> callback) {
        this.onUbicacionGuardada = callback;
    }

    @FXML
    private void guardarUbicacion() {
        String lugar = txtLugar.getText().trim();
        String zona = txtZona.getText().trim();
        String indicaciones = txtIndicaciones.getText().trim();

        if (lugar.isEmpty()) {
            lblError.setText(Mensajes.LUGAR_OBLIGATORIO);
            return;
        }

        Ubicacion ubicacion = ubicacionServicio.guardarSiNoExiste(lugar, zona, indicaciones);

        if (onUbicacionGuardada != null) {
            onUbicacionGuardada.accept(ubicacion);
        }

        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtLugar.getScene().getWindow();
        stage.close();
    }
}

