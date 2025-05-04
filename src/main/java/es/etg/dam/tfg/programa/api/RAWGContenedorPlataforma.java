package es.etg.dam.tfg.programa.api;

import lombok.Data;
import com.google.gson.annotations.SerializedName;

@Data
public class RAWGContenedorPlataforma {
    @SerializedName("platform")
    private RAWGPlataforma platform;
}
