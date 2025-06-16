package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
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

public class HistoryOnServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();
    String local = "http://localhost:8080/history";

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        Task task = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Epic epic = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusDays(3), epic);
        epic.addSubtask(subtask);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void closeUp() {
        httpTaskServer.stop(0);
    }

    @Test
    void serverShouldGiveHistory() throws IOException, InterruptedException {
        String historyToJson = gson.toJson(taskManager.getHistory());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(local))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(historyToJson, response.body());
    }

}
