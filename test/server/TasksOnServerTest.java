package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.interfaces.TaskManager;
import model.Task;
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

public class TasksOnServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();
    String local = "http://localhost:8080/tasks";

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
    void tasksShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(taskInJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(task1, taskManager.getTask(1));

    }

    @Test
    void tasksShouldAddsToManagerThroughServerWithId() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(taskInJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .POST(HttpRequest.BodyPublishers.ofString(taskInJson))
                .build();
        HttpResponse<String> response1 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response1.statusCode());
    }

    @Test
    void errorIfOverlapTasks() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = gson.toJson(task1);
        HttpRequest firstRequest = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(taskInJson))
                .build();
        HttpResponse<String> firstResponse = client.send(firstRequest, HttpResponse.BodyHandlers.ofString());
        Task task2 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        String overlapTaskJson = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .POST(HttpRequest.BodyPublishers.ofString(overlapTaskJson))
                .build();
        HttpResponse<String> secondResponse = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, secondResponse.statusCode());
    }

    @Test
    void serverShouldGiveAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Task task2 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        Task task3 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(3));
        Task task4 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(45));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);

        URI url = URI.create(local);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String tasksToJson = gson.toJson(taskManager.getTasks());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(tasksToJson, response.body());
    }

    @Test
    void serverShouldGiveTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task1);
        String taskInJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskInJson, response.body());
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
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local + "/1"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
    }
}
