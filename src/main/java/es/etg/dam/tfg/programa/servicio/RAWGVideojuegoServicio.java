package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.api.RAWGRespuesta;
import es.etg.dam.tfg.programa.api.RAWGServicio;
import es.etg.dam.tfg.programa.api.RAWGVideojuego;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value; 

@Service
public class RAWGVideojuegoServicio { 

    private final RAWGServicio rawgServicio;
    private final String apiKey;

    @Autowired
    public RAWGVideojuegoServicio(RAWGServicio rawgServicio, @Value("${rawg.api.key}") String apiKey) {
        this.rawgServicio = rawgServicio;
        this.apiKey = apiKey;
    }

    public RAWGRespuesta<RAWGVideojuego> buscarJuegos(String query, int page, int pageSize) throws IOException {
        Call<RAWGRespuesta<RAWGVideojuego>> call = rawgServicio.getGames(query, page, pageSize, apiKey); 
        Response<RAWGRespuesta<RAWGVideojuego>> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new IOException("Error al buscar juegos: " + response.message());
        }
    }

    public RAWGVideojuego obtenerDetallesJuego(int id) throws IOException {
        Call<RAWGVideojuego> call = rawgServicio.getGameDetails(id, apiKey); 
        Response<RAWGVideojuego> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new IOException("Error al obtener detalles del juego: " + response.message());
        }
    }
}
