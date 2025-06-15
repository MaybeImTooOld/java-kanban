import manager.exceptions.TaskOverlapException;
import manager.interfaces.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    // Абстрактный метод для создания конкретной реализации менеджера
    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();

    }


    @Test
    void shouldCreateTask() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task 1", "Description", 30, LocalDateTime.now());
        taskManager.addNewTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void shouldNotAllowTaskTimeOverlap() throws TaskOverlapException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Description", 30, LocalDateTime.now());
        Task task2 = new Task(TaskStatus.NEW, "Task 2", "Description", 60, LocalDateTime.now().minusMinutes(40));

        taskManager.addNewTask(task1);
        assertThrows(TaskOverlapException.class, () -> taskManager.addNewTask(task2));
    }

}