package es.etg.dam.tfg.programa.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.List;

@Data
public class RAWGVideojuego {
    private int id;
    private String name;
    @SerializedName("released")
    private String fechaLanzamiento;
    private double rating;
    @SerializedName("background_image")
    private String backgroundImage;
    private List<RAWGGenero> genres;
    private List<RAWGContenedorPlataforma> platforms; // Contenedor para guardar la lista de plataformas que devuelve RAWG.
    private String description;
    private int metacritic;
}
