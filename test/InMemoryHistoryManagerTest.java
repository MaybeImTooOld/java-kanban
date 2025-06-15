import manager.Managers;
import manager.interfaces.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Task task2 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        Task task3 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now().minusDays(3));
        Task task4 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(45));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);
    }

    @Test
    public void oldIdsShouldNotBeKeptInManager() {
        taskManager.getTask(1);
        taskManager.getTask(4);
        taskManager.getTask(3);
        taskManager.getTask(2);
        List<Task> buffer = taskManager.getHistory();
        System.out.println(buffer);
        taskManager.deleteById(3);
        System.out.println(taskManager.getHistory());
        Assertions.assertNotEquals(buffer, taskManager.getHistory());

    }

}
