package es.etg.dam.tfg.programa.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.List;

@Data
public class RAWGRespuesta<T> {
    @SerializedName("results")
    private List<T> results;
    @SerializedName("next")
    private String next;
    @SerializedName("previous")
    private String previous;
     @SerializedName("count")
    private int count;
}