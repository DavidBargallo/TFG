package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.api.RAWGServicio;
import es.etg.dam.tfg.programa.api.RAWGVideojuego;
import es.etg.dam.tfg.programa.api.RAWGRespuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;

@Service
public class RAWGVideojuegoServicio {

    private final RAWGServicio rawgServicio;

    @Autowired
    public RAWGVideojuegoServicio(RAWGServicio rawgServicio) {
        this.rawgServicio = rawgServicio;
    }

    public RAWGRespuesta<RAWGVideojuego> buscarJuegos(String query, int page, int pageSize) throws IOException {
        Call<RAWGRespuesta<RAWGVideojuego>> call = rawgServicio.getGames(query, page, pageSize);
        Response<RAWGRespuesta<RAWGVideojuego>> response = call.execute(); // Síncrono para simplificar, pero puedes usar asíncrono
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new IOException("Error al buscar juegos: " + response.message());
        }
    }

    public RAWGVideojuego obtenerDetallesJuego(int id) throws IOException {
        Call<RAWGVideojuego> call = rawgServicio.getGameDetails(id);
        Response<RAWGVideojuego> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new IOException("Error al obtener detalles del juego: " + response.message());
        }
    }
}
