package server;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class EpicsOnServerTest extends OnServerTestAbstract {

    @BeforeAll
    static void setUrl() {
        setUrlForClass("epics");
    }

    @Test
    void epicsShouldAddsToManagerThroughServerWithoutId() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        epic.setId(1);
        String epicInJson = Serializator.gsonForTasks.toJson(epic);
        HttpResponse<String> response = sendPostToServer(epicInJson);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(epic, taskManager.getEpic(1));

    }


    @Test
    void errorIfOverlapEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        epic.setId(1);
        String epicInJson = Serializator.gsonForTasks.toJson(epic);
        sendPostToServer(epicInJson);
        Epic epic1 = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        String overlapEpicJson = Serializator.gsonForTasks.toJson(epic1);
        HttpResponse<String> secondResponse = sendPostToServer(overlapEpicJson);
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
        HttpResponse<String> response = sendGetToServer();
        String epicsToJson = Serializator.gsonForTasks.toJson(taskManager.getEpics());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epicsToJson, response.body());
    }

    @Test
    void serverShouldGiveEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewEpic(epic);
        String epicInJson = Serializator.gsonForTasks.toJson(epic);
        HttpResponse<String> response = sendGetToServer(epic);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epicInJson, response.body());
    }

    @Test
    void serverShouldGiveErrorIfEpicWithThisIdIsNotExisting() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetToServer(1);
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void serverShouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewEpic(epic);
        HttpResponse<String> response = sendDeleteToServer(epic);
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
        String epicsSubtasks = Serializator.gsonForTasks.toJson(epic.getSubtasks());
        HttpResponse<String> response = sendGetToServer(1, "/subtasks");
        Assertions.assertEquals(epicsSubtasks, response.body());
    }

    @Test
    void serverShouldGiveErrorIfEpicWithThisIdAndForSubtasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetToServer(1, "/subtasks");
        Assertions.assertEquals(404, response.statusCode());
    }
}
