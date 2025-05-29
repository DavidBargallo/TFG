package es.etg.dam.tfg.programa.utils;

import com.fasterxml.jackson.databind.JsonNode;

import es.etg.dam.tfg.programa.controlador.NuevaUbicacionControlador;
import es.etg.dam.tfg.programa.modelo.*;
import es.etg.dam.tfg.programa.servicio.ConsolaServicio;
import es.etg.dam.tfg.programa.servicio.UbicacionServicio;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class FormularioJuegoUtils {

    public static boolean preguntarFormatoJuego() {
        List<String> opciones = List.of("Físico", "Digital");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Físico", opciones);
        dialog.setTitle("Seleccionar formato");
        dialog.setHeaderText("¿Cómo tienes este juego?");
        dialog.setContentText("Formato:");
        return dialog.showAndWait()
                .orElseThrow(() -> new RuntimeException("Selección cancelada."))
                .equals("Físico");
    }

    public static Ubicacion obtenerUbicacionParaJuegoFisico(
        UbicacionServicio ubicacionServicio,
        ApplicationContext context) {

    List<Ubicacion> ubicaciones = ubicacionServicio.obtenerTodas();

    if (!ubicaciones.isEmpty()) {
        ChoiceDialog<Ubicacion> dialog = new ChoiceDialog<>(ubicaciones.get(0), ubicaciones);
        dialog.setTitle("Seleccionar ubicación");
        dialog.setHeaderText("Selecciona una ubicación existente o cancela para crear una nueva:");
        dialog.setContentText("Ubicación:");
        Optional<Ubicacion> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            return resultado.get();
        }
    }

    final Ubicacion[] nuevaUbicacion = new Ubicacion[1];
    final Stage[] stageWrapper = new Stage[1];

    stageWrapper[0] = FXMLSoporte.abrirFormularioUbicacion(
            context,
            RutaFXML.NUEVA_UBICACION,
            "Nueva Ubicación",
            (NuevaUbicacionControlador c) -> c.setOnUbicacionGuardada(u -> {
                nuevaUbicacion[0] = u;
                stageWrapper[0].close();
            }));

    if (stageWrapper[0] == null) {
        throw new RuntimeException("No se pudo abrir el formulario de nueva ubicación.");
    }

    stageWrapper[0].showAndWait();

    if (nuevaUbicacion[0] != null) {
        return nuevaUbicacion[0];
    }

    throw new RuntimeException("Ubicación no establecida.");
}


    public static Consola seleccionarConsolaDesdeVideojuego(Videojuego juego, ConsolaServicio consolaServicio) {
        List<Consola> disponibles = consolaServicio.obtenerTodas();
        if (disponibles.isEmpty()) {
            throw new RuntimeException("No hay consolas disponibles.");
        }

        List<String> nombres = disponibles.stream().map(Consola::getNombre).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Seleccionar consola");
        dialog.setHeaderText("Selecciona la consola en la que tienes este juego:");
        dialog.setContentText("Consola:");

        return dialog.showAndWait()
                .flatMap(nombre -> disponibles.stream().filter(c -> c.getNombre().equals(nombre)).findFirst())
                .orElseThrow(() -> new RuntimeException("Consola no seleccionada."));
    }

    public static Consola seleccionarConsolaDesdeJson(JsonNode json, ConsolaServicio consolaServicio) {
        List<Consola> consolas = new ArrayList<>();
        if (json.has("platforms")) {
            json.get("platforms").forEach(n -> {
                String nombre = n.get("platform").get("name").asText();
                consolaServicio.obtenerTodas().stream()
                        .filter(c -> c.getNombre().equals(nombre))
                        .findFirst()
                        .ifPresent(consolas::add);
            });
        }

        if (consolas.isEmpty()) throw new RuntimeException("No se encontraron consolas válidas.");

        if (consolas.size() == 1) return consolas.get(0);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(consolas.get(0).getNombre(),
                consolas.stream().map(Consola::getNombre).toList());
        dialog.setTitle("Seleccionar consola");
        dialog.setHeaderText("Elige una consola para agregar el juego:");
        dialog.setContentText("Consola:");

        return dialog.showAndWait()
                .flatMap(nombre -> consolas.stream().filter(c -> c.getNombre().equals(nombre)).findFirst())
                .orElseThrow(() -> new RuntimeException("Consola no encontrada."));
    }
}

