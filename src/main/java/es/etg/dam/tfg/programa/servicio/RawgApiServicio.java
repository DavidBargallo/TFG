package es.etg.dam.tfg.programa.servicio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class RawgApiServicio {

    private static final String API_KEY = "436d7168dd0e4ccd8503b6ad89624e45";
    private static final String BASE_URL = "https://api.rawg.io/api";

    private final ObjectMapper objectMapper;

    public JsonNode buscarJuegos(String query) throws IOException, InterruptedException {
        String url = String.format("%s/games?search=%s&page_size=10&key=%s", BASE_URL, query.replace(" ", "%20"), API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body()).get("results");
    }

    public JsonNode obtenerJuegoPorId(String id) throws IOException, InterruptedException {
        String url = String.format("%s/games/%s?key=%s", BASE_URL, id, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body());
    }
}

