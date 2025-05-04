package es.etg.dam.tfg.programa.api;

//API Key: 436d7168dd0e4ccd8503b6ad89624e45
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RAWGServicio {
    @GET("games")
    Call<RAWGRespuesta<RAWGVideojuego>> getGames(
        @Query("search") String search,
        @Query("page") int page,
        @Query("page_size") int pageSize,
        @Query("key") String apiKey
    );

    @GET("games/{id}")
    Call<RAWGVideojuego> getGameDetails(
        @Path("id") int id,
        @Query("key") String apiKey
    );
}
