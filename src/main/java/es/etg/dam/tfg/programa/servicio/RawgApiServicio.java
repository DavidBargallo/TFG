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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawgApiServicio {

    private static final String API_KEY = "436d7168dd0e4ccd8503b6ad89624e45";
    private static final String BASE_URL = "https://api.rawg.io/api";

    private final ObjectMapper objectMapper;

    public JsonNode buscarJuegos(String nombre, Integer generoId, Integer plataformaId, String orden, int page, int pageSize)
        throws IOException, InterruptedException {

    StringBuilder url = new StringBuilder(BASE_URL + "/games?");
    url.append("page=").append(page)
       .append("&page_size=").append(pageSize)
       .append("&key=").append(API_KEY);

    if (nombre != null && !nombre.isBlank()) {
        url.append("&search=").append(URLEncoder.encode(nombre, StandardCharsets.UTF_8));
    }
    if (generoId != null) {
        url.append("&genres=").append(generoId);
    }
    if (plataformaId != null) {
        url.append("&platforms=").append(plataformaId);
    }
    if (orden != null) {
        if (orden.equals("Fecha más reciente")) {
            url.append("&ordering=-released");
        } else if (orden.equals("Fecha más antigua")) {
            url.append("&ordering=released");
        }
    }

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .GET()
            .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return objectMapper.readTree(response.body());
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

    public List<String> obtenerGenerosDesdeApi() throws IOException, InterruptedException {
    List<String> generos = new ArrayList<>();
    int pagina = 1;

    while (true) {
        JsonNode respuesta = consumirApi("https://api.rawg.io/api/genres?page=" + pagina);
        JsonNode resultados = respuesta.get("results");

        if (resultados == null || !resultados.isArray() || resultados.size() == 0) {
            break;
        }

        for (JsonNode genero : resultados) {
            generos.add(genero.get("name").asText());
        }

        if (respuesta.get("next").isNull()) {
            break;
        }

        pagina++;
    }

    return generos;
}

public List<String> obtenerPlataformasDesdeApi() throws IOException, InterruptedException {
    List<String> plataformas = new ArrayList<>();
    String url = "https://api.rawg.io/api/platforms?key=" + API_KEY;
    
    while (url != null) {
        JsonNode respuesta = consumirApi(url);
        for (JsonNode plataforma : respuesta.get("results")) {
            plataformas.add(plataforma.get("name").asText());
        }
        url = respuesta.has("next") && !respuesta.get("next").isNull()
                ? respuesta.get("next").asText()
                : null;
    }

    return plataformas;
}


    public JsonNode consumirApi(String url) throws IOException, InterruptedException {
        if (!url.contains("key=")) {
            url += (url.contains("?") ? "&" : "?") + "key=" + API_KEY;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error al consumir la API: " + response.statusCode() + " - " + response.body());
        }

        return objectMapper.readTree(response.body());
    }
}
