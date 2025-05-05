package es.etg.dam.tfg.programa.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import es.etg.dam.tfg.programa.servicio.RawgApiServicio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ControladorBusqueda {

    @FXML
    private ComboBox<String> comboConsolaApi;

    @FXML
    private TextField txtNombreApi;

    @FXML
    private TextField txtPrecioMaxApi;

    @FXML
    private ComboBox<String> comboOrdenApi;

    @FXML
    private VBox contenedorResultadosApi;

    @FXML
    private Button btnBuscarApi;

    private final RawgApiServicio rawgApiServicio;

    @FXML
    public void initialize() {
        comboConsolaApi.getItems().addAll("PC", "PlayStation", "Xbox", "Nintendo", "Mobile");
        comboOrdenApi.getItems().addAll("Relevancia", "Precio", "Fecha de lanzamiento");
        btnBuscarApi.setOnAction(this::buscarJuegos);
    }

    private void buscarJuegos(ActionEvent event) {
        String nombre = txtNombreApi.getText().trim();
        String consola = comboConsolaApi.getValue();
        String precioMax = txtPrecioMaxApi.getText().trim();
        String ordenarPor = comboOrdenApi.getValue();

        try {
            JsonNode juegos = rawgApiServicio.buscarJuegos(nombre);

            contenedorResultadosApi.getChildren().clear();

            if (juegos != null && juegos.size() > 0) {
                for (JsonNode juego : juegos) {
                    String nombreJuego = juego.get("name").asText();
                    String imagenUrl = juego.get("background_image").asText();

                    HBox hBox = new HBox(10);
                    hBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
                    ImageView imageView = new ImageView(new Image(imagenUrl, 120, 80, true, true));

                    Label labelJuego = new Label(nombreJuego);
                    hBox.getChildren().addAll(imageView, labelJuego);
                    contenedorResultadosApi.getChildren().add(hBox);
                }
            } else {
                contenedorResultadosApi.getChildren().add(new Label("No se encontraron juegos."));
            }
        } catch (Exception e) {
            contenedorResultadosApi.getChildren().add(new Label("Error al buscar juegos: " + e.getMessage()));
        }
    }
}
