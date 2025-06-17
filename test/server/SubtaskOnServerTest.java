package server;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class SubtaskOnServerTest extends OnServerTestAbstract {

    String local = "http://localhost:8080/subtasks";
    Epic epic;

    @Override
    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(25));

    }

    @Test
    void subtasksShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        subtask1.setId(1);
        String subtaskInJson = Serializator.gsonForTasks.toJson(subtask1);
        HttpResponse<String> response = sendPostToServer(local, subtaskInJson);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(subtask1, taskManager.getSubtask(1));

    }


    @Test
    void errorIfOverlapSubtasks() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        subtask.setId(1);
        String subtaskInJson = Serializator.gsonForTasks.toJson(subtask);
        sendPostToServer(local, subtaskInJson);
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        String overlapSubtaskJson = Serializator.gsonForTasks.toJson(subtask1);
        HttpResponse<String> secondResponse = sendPostToServer(local, overlapSubtaskJson);
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

        HttpResponse<String> response = sendGetToServer(local);
        String subtasksToJson = Serializator.gsonForTasks.toJson(taskManager.getSubtasks());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtasksToJson, response.body());
    }

    @Test
    void serverShouldGiveTSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        taskManager.addNewSubtask(subtask);
        String subtaskInJson = Serializator.gsonForTasks.toJson(subtask);
        HttpResponse<String> response = sendGetToServer(local + "/1");
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtaskInJson, response.body());
    }

    @Test
    void serverShouldGiveErrorIfTaskWithThisIdIsNotExisting() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetToServer(local + "/1");
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void serverShouldDeleteTaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now(), epic);
        taskManager.addNewTask(subtask);
        HttpResponse<String> response = sendDeleteToServer(local + "/1");
        Assertions.assertEquals(200, response.statusCode());
    }
}
