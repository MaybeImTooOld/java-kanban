package server;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class PrioritizedOnServerTest extends OnServerTestAbstract {

    @BeforeAll
    static void setUrl() {
        setUrlForClass("prioritized");
    }

    @Override
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
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @Test
    void serverShouldGivePrioritized() throws IOException, InterruptedException {
        String prioritizedToJson = Serializator.gsonForTasks.toJson(taskManager.getPrioritizedTasks());
        HttpResponse<String> response = sendGetToServer();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(prioritizedToJson, response.body());
    }
}
