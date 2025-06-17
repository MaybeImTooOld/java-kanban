import manager.InMemoryTaskManager;
import manager.exceptions.TaskOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldDeleteTasksById() throws TaskOverlapException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Task task2 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.deleteById(1);
        taskManager.deleteById(2);

        assertTrue(taskManager.getTasks().isEmpty(), "Задачи не были удалены");
    }

    @Test
    void shouldUpdateTaskStatus() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task", "Desc", 30, null);
        taskManager.addNewTask(task);
        int taskId = task.getId();
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(task, taskId);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(taskId).getStatus());
    }

    @Test
    public void taskManagerShouldAddNewTasksAndFindItById() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        Epic epic = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        Subtask subtask = new Subtask(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusDays(3), epic);
        epic.addSubtask(subtask);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        assertEquals(task, taskManager.getTask(1));
        assertEquals(epic, taskManager.getEpic(2));
        assertEquals(subtask, taskManager.getSubtask(3));
    }

    @Test
    public void tasksWithGeneratedIdAndGivenIdShouldNotConflict() throws TaskOverlapException {
        Task task1 = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task1);
        Task task2 = new Task(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        task2.setId(2);
        taskManager.addNewTask(task2);
        assertNotNull(taskManager.getTask(2));
    }

    @Test
    public void taskShouldNotChangeAfterAdding() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task);
        Task taskCloneAfterAdding = taskManager.getTask(1);
        assertEquals(task.getName(), taskCloneAfterAdding.getName());
        assertEquals(task.getStatus(), taskCloneAfterAdding.getStatus());
        assertEquals(task.getDescription(), taskCloneAfterAdding.getDescription());
    }

    @Test
    public void historyManagerShouldSavePreviousVersionOfTask() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task 1", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task);
        taskManager.getTask(1);
        List<Task> tasksInHistory = taskManager.getHistory();
        assertTrue(tasksInHistory.contains(task));
    }
}