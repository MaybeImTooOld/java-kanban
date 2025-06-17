package server;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class TasksOnServerTest extends OnServerTestAbstract {

    @BeforeAll
    static void setUrl() {
        setUrlForClass("tasks");
    }

    @Test
    void tasksShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = Serializator.gsonForTasks.toJson(task1);
        HttpResponse<String> response = sendPostToServer(taskInJson);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(task1, taskManager.getTask(1));

    }

    @Test
    void tasksShouldAddsToManagerThroughServerWithId() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = Serializator.gsonForTasks.toJson(task1);
        sendPostToServer(taskInJson);
        HttpResponse<String> response = sendPostToServer(task1, taskInJson);
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    void errorIfOverlapTasks() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        task1.setId(1);
        String taskInJson = Serializator.gsonForTasks.toJson(task1);
        sendPostToServer(taskInJson);
        Task task2 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        String overlapTaskJson = Serializator.gsonForTasks.toJson(task2);
        HttpResponse<String> secondResponse = sendPostToServer(overlapTaskJson);
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

        HttpResponse<String> response = sendGetToServer();
        String tasksToJson = Serializator.gsonForTasks.toJson(taskManager.getTasks());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(tasksToJson, response.body());
    }

    @Test
    void serverShouldGiveTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task1);
        String taskInJson = Serializator.gsonForTasks.toJson(task1);
        HttpResponse<String> response = sendGetToServer(task1);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskInJson, response.body());
    }

    @Test
    void serverShouldGiveErrorIfTaskWithThisIdIsNotExisting() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetToServer(1);
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void serverShouldDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task1);
        HttpResponse<String> response = sendDeleteToServer(1);
        Assertions.assertEquals(200, response.statusCode());
    }
}
