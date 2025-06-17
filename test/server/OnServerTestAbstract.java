package server;

import manager.Managers;
import manager.interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class OnServerTestAbstract {
    protected static String local;
    protected HttpTaskServer httpTaskServer;
    protected TaskManager taskManager;
    protected HttpClient client = HttpClient.newHttpClient();

    static void setUrlForClass(String className) {
        local = "http://localhost:8080/" + className;
    }

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

    HttpResponse<String> sendGetToServer() throws IOException, InterruptedException {
        URI url = URI.create(local);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendGetToServer(Task task) throws IOException, InterruptedException {
        URI url = URI.create(getUrlWithId(task));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendGetToServer(int id) throws IOException, InterruptedException {
        URI url = URI.create(getUrlWithId(id));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendGetToServer(int id, String str) throws IOException, InterruptedException {
        URI url = URI.create(getUrlWithId(id) + str);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendPostToServer(String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendPostToServer(Task task, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrlWithId(task)))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendDeleteToServer(Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrlWithId(task)))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendDeleteToServer(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrlWithId(id)))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    String getUrlWithId(Task task) {
        return local + "/" + task.getId();
    }

    String getUrlWithId(int id) {
        return local + "/" + id;
    }
}
