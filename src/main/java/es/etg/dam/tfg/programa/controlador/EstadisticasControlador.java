package es.etg.dam.tfg.programa.controlador;

import es.etg.dam.tfg.programa.modelo.UsuarioVideojuego;
import es.etg.dam.tfg.programa.servicio.UsuarioVideojuegoServicio;
import es.etg.dam.tfg.programa.utils.FXMLSoporte;
import es.etg.dam.tfg.programa.utils.Mensajes;
import es.etg.dam.tfg.programa.utils.RutaFXML;
import es.etg.dam.tfg.programa.utils.Sesion;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
        private Menu menuCuenta;

        @FXML
        public void initialize() {
                Sesion.nombreUsuario(menuCuenta);
                var usuario = Sesion.getUsuarioActual();
                if (usuario == null)
                        return;

                List<UsuarioVideojuego> juegos = usuarioVideojuegoServicio
                                .obtenerVideojuegosPorUsuario(usuario.getId())
                                .stream()
                                .filter(uv -> !uv.isEnWishlist())
                                .toList();

                if (juegos.isEmpty()) {
                        lblGeneroFavorito.setText(String.format(Mensajes.GENERO_MAS_FRECUENTE, Mensajes.SIN_DATOS));
                        lblConsolaFavorita.setText(String.format(Mensajes.CONSOLA_MAS_FRECUENTE, Mensajes.SIN_DATOS));
                        lblTotalFisicos.setText(String.format(Mensajes.JUEGOS_FISICOS, 0));
                        lblTotalDigitales.setText(String.format(Mensajes.JUEGOS_DIGITALES, 0));

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

                lblGeneroFavorito.setText(String.format(Mensajes.GENERO_MAS_FRECUENTE, obtenerMasFrecuente(generos)));
                lblConsolaFavorita
                                .setText(String.format(Mensajes.CONSOLA_MAS_FRECUENTE, obtenerMasFrecuente(consolas)));
                lblTotalFisicos.setText(String.format(Mensajes.JUEGOS_FISICOS, totalFisicos));
                lblTotalDigitales.setText(String.format(Mensajes.JUEGOS_DIGITALES, totalDigitales));

                List<PieChart.Data> datosGeneros = generos.entrySet().stream()
                                .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                                .toList();
                graficoGeneros.setData(javafx.collections.FXCollections.observableArrayList(datosGeneros));

                XYChart.Series<String, Number> serieConsolas = new XYChart.Series<>();
                serieConsolas.setName(Mensajes.CONSOLA2);
                consolas.forEach((nombre, cantidad) -> serieConsolas.getData()
                                .add(new XYChart.Data<>(nombre, cantidad)));

                graficoConsolas.getData().clear();
                graficoConsolas.getData().add(serieConsolas);
        }

        private String obtenerMasFrecuente(Map<String, Long> mapa) {
                return mapa.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse(Mensajes.SIN_DATOS);
        }

        @FXML
        private void volverABiblioteca() {
                Stage stageActual = (Stage) lblGeneroFavorito.getScene().getWindow();

                FXMLSoporte.abrirEInicializar(
                                applicationContext,
                                RutaFXML.BIBLIOTECA,
                                Mensajes.TITULO_BIBLIOTECA,
                                stageActual,
                                (BibliotecaControlador controlador) -> controlador.inicializarBiblioteca());
        }
}
