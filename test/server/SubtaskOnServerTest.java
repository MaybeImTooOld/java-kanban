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

public class SubtaskOnServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();
    String local = "http://localhost:8080/subtasks";
    Epic epic;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(25));

    }

    @AfterEach
    void closeUp() {
        httpTaskServer.stop(0);
    }

    @Test
    void subtasksShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        subtask1.setId(1);
        String subtaskInJson = gson.toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskInJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(subtask1, taskManager.getSubtask(1));

    }


    @Test
    void errorIfOverlapSubtasks() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        subtask.setId(1);
        String taskInJson = gson.toJson(subtask);
        HttpRequest firstRequest = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(taskInJson))
                .build();
        HttpResponse<String> firstResponse = client.send(firstRequest, HttpResponse.BodyHandlers.ofString());
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        String overlapSubtaskJson = gson.toJson(subtask1);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(overlapSubtaskJson))
                .build();
        HttpResponse<String> secondResponse = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, secondResponse.statusCode());
    }

    @Test
    void serverShouldGiveAllTSubtasks() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1), epic);
        Subtask subtask2 = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(3), epic);
        Subtask subtask3 = new Subtask(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(45), epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        URI url = URI.create(local);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String subtasksToJson = gson.toJson(taskManager.getSubtasks());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtasksToJson, response.body());
    }

    @Test
    void serverShouldGiveTSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        taskManager.addNewSubtask(subtask);
        String subtaskInJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtaskInJson, response.body());
    }

    @Test
    void serverShouldGiveErrorIfTaskWithThisIdIsNotExisting() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void serverShouldDeleteTaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        taskManager.addNewTask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
    }
}
