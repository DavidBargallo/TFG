package es.etg.dam.tfg.programa.api;

import java.util.List;

import lombok.Data;

@Data
public class RAWGVideojuego {
    public int id;
    public String name;
    public String released;
    public String background_image;
    public List<PlatformInfo> platforms;
    public List<GenreInfo> genres;

    public static class PlatformInfo {
        public Platform platform;

        public static class Platform {
            public int id;
            public String name;
        }
    }

    public static class GenreInfo {
        public int id;
        public String name;
    }
}

