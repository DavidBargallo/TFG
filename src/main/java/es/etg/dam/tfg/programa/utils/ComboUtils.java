package es.etg.dam.tfg.programa.utils;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComboUtils {

    public static void cargarComboConNombres(ComboBox<String> combo, String primerElemento, Collection<String> elementos) {
        combo.getItems().clear();
        combo.getItems().add(primerElemento);
        combo.getItems().addAll(elementos.stream().sorted().collect(Collectors.toList()));
        combo.getSelectionModel().selectFirst();
    }

    public static void cargarComboDesdeJson(ComboBox<String> combo, String primerElemento, JsonNode lista, Function<JsonNode, String> extractorNombre) {
        combo.getItems().clear();
        combo.getItems().add(primerElemento);

        for (JsonNode item : lista) {
            String nombre = extractorNombre.apply(item);
            combo.getItems().add(nombre);
        }

        combo.getSelectionModel().selectFirst();
    }
}
