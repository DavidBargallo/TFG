package es.etg.dam.tfg.programa.api.retrofit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.etg.dam.tfg.programa.api.RAWGServicio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Configuration
public class RAWGCliente {

    private static final String BASE_URL = "https://api.rawg.io/api/";

    @Bean
    public RAWGServicio rawgService(@Value("${rawg.api.key}") String apiKey) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(chain -> {
                okhttp3.Request request = chain.request().newBuilder()
                    .url(chain.request().url().newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build())
                    .build();
                return chain.proceed(request);
            })
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        return retrofit.create(RAWGServicio.class);
    }
}