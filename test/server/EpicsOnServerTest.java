package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.adapters.JavaTimeAdapters;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicsOnServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();
    String local = "http://localhost:8080/epics";

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

    @Test
    void epicsShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        epic.setId(1);
        String epicInJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(epicInJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(epic, taskManager.getEpic(1));

    }


    @Test
    void errorIfOverlapEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        epic.setId(1);
        String epicInJson = gson.toJson(epic);
        HttpRequest firstRequest = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(epicInJson))
                .build();
        HttpResponse<String> firstResponse = client.send(firstRequest, HttpResponse.BodyHandlers.ofString());
        Epic epic1 = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        String overlapEpicJson = gson.toJson(epic1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(overlapEpicJson))
                .build();
        HttpResponse<String> secondResponse = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, secondResponse.statusCode());
    }

    @Test
    void serverShouldGiveAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Epic epic1 = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        Epic epic2 = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(3));
        Epic epic3 = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(45));
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        URI url = URI.create(local);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String epicsToJson = gson.toJson(taskManager.getEpics());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epicsToJson, response.body());
    }

    @Test
    void serverShouldGiveEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewEpic(epic);
        String epicInJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epicInJson, response.body());
    }

    @Test
    void serverShouldGiveErrorIfEpicWithThisIdIsNotExisting() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void serverShouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void serverShouldGiveAllEpicsSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Subtask subtask = new Subtask(TaskStatus.NEW, "Sub", "desc", 40, LocalDateTime.now().plusDays(2), epic);
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Sub", "desc", 40, LocalDateTime.now().minusDays(2), epic);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask1);
        String epicsSubtasks = gson.toJson(epic.getSubtasks());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(epicsSubtasks, response.body());
    }

    @Test
    void serverShouldGiveErrorIfEpicWithThisIdAndForSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}
