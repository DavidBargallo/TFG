package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.servicio.UsuarioVideojuegoServicio;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EstadisticasControlador {

    private final UsuarioVideojuegoServicio usuarioVideojuegoServicio;
    private final ApplicationContext applicationContext;

    @FXML
    private Label lblGeneroFavorito, lblConsolaFavorita, lblTotalFisicos, lblTotalDigitales;

    @FXML
    private MenuItem menuVolverBiblioteca;

    @FXML
    private PieChart graficoGeneros;

    @FXML
    private BarChart<String, Number> graficoConsolas;

    @FXML
    public void initialize() {
        var usuario = Sesion.getUsuarioActual();
        if (usuario == null)
            return;

        List<UsuarioVideojuego> juegos = usuarioVideojuegoServicio.obtenerVideojuegosPorUsuario(usuario.getId());

        if (juegos.isEmpty()) {
            lblGeneroFavorito.setText("Género más frecuente: (sin datos)");
            lblConsolaFavorita.setText("Consola más frecuente: (sin datos)");
            lblTotalFisicos.setText("Juegos físicos: 0");
            lblTotalDigitales.setText("Juegos digitales: 0");
            return;
        }

        Map<String, Long> generos = juegos.stream()
                .flatMap(uv -> uv.getVideojuego().getGeneros().stream())
                .collect(Collectors.groupingBy(g -> g.getNombre(), Collectors.counting()));

        Map<String, Long> consolas = juegos.stream()
                .flatMap(uv -> uv.getVideojuego().getConsolas().stream())
                .collect(Collectors.groupingBy(c -> c.getNombre(), Collectors.counting()));

        long totalFisicos = juegos.stream().filter(uv -> uv.getVideojuego().isEsFisico()).count();
        long totalDigitales = juegos.size() - totalFisicos;

        lblGeneroFavorito.setText("Género más frecuente: " + obtenerMasFrecuente(generos));
        lblConsolaFavorita.setText("Consola más frecuente: " + obtenerMasFrecuente(consolas));
        lblTotalFisicos.setText("Juegos físicos: " + totalFisicos);
        lblTotalDigitales.setText("Juegos digitales: " + totalDigitales);

        List<PieChart.Data> datosGeneros = generos.entrySet().stream()
                .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                .toList();
        graficoGeneros.setData(javafx.collections.FXCollections.observableArrayList(datosGeneros));

        XYChart.Series<String, Number> serieConsolas = new XYChart.Series<>();
        serieConsolas.setName("Consolas");

        consolas.forEach((nombre, cantidad) -> serieConsolas.getData().add(new XYChart.Data<>(nombre, cantidad)));

        graficoConsolas.getData().clear();
        graficoConsolas.getData().add(serieConsolas);

    }

    private String obtenerMasFrecuente(Map<String, Long> mapa) {
        return mapa.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("(sin datos)");
    }

    @FXML
    private void volverABiblioteca() {
        try {
            URL url = getClass().getResource("/vista/pantalla_biblioteca.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            BibliotecaControlador controlador = loader.getController();
            controlador.inicializarBiblioteca();

            Stage stage = (Stage) lblGeneroFavorito.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Biblioteca");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
