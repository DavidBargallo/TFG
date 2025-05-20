package es.etg.dam.tfg.programa.utils;

import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComboUtils {

    public static void cargarComboConNombres(ComboBox<String> combo, String primerElemento, Collection<String> elementos) {
        combo.getItems().clear();
        combo.getItems().add(primerElemento);
        combo.getItems().addAll(elementos.stream().sorted().collect(Collectors.toList()));
        combo.getSelectionModel().selectFirst();
    }

    public static <T> Map<String, Integer> cargarComboDesdeObjetos(
        ComboBox<String> combo,
        String primerElemento,
        Collection<T> elementos,
        Function<T, String> nombreExtractor,
        Function<T, Integer> idExtractor
) {
    combo.getItems().clear();
    combo.getItems().add(primerElemento);

    Map<String, Integer> mapa = new HashMap<>();
    for (T elem : elementos) {
        String nombre = nombreExtractor.apply(elem);
        Integer id = idExtractor.apply(elem);
        combo.getItems().add(nombre);
        mapa.put(nombre, id);
    }

    combo.getSelectionModel().selectFirst();
    return mapa;
}

}
