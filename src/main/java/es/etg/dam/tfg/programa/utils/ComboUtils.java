package es.etg.dam.tfg.programa.utils;

import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.stream.Collectors;

public class ComboUtils {

    public static void cargarComboConNombres(ComboBox<String> combo, String primerElemento, Collection<String> elementos) {
        combo.getItems().clear();
        combo.getItems().add(primerElemento);
        combo.getItems().addAll(elementos.stream().sorted().collect(Collectors.toList()));
        combo.getSelectionModel().selectFirst();
    }
}

