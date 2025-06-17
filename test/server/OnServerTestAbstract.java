package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.adapters.JavaTimeAdapters;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class OnServerTestAbstract {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

    }

    @AfterEach
    void closeUp() {
        httpTaskServer.stop(0);
    }

    HttpResponse<String> sendGetToServer(String uri) throws IOException, InterruptedException {
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendPostToServer(String uri, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendDeleteToServer(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
