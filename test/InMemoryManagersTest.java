import Manager.Interfaces.TaskManager;
import Manager.Managers;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryManagersTest {
    TaskManager taskManager;

    @BeforeEach
    public void CreateTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void TaskManagerShouldAddNewTasksAndFindItById() {
        Task task = new Task(TaskStatus.NEW, "description", "name");
        Epic epic = new Epic(TaskStatus.NEW, "description", "name");
        Subtask subtask = new Subtask(TaskStatus.NEW, "description", "name", epic);
        epic.addSubtask(subtask);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        assertEquals(task, taskManager.getTask(1));
        assertEquals(epic, taskManager.getEpic(2));
        assertEquals(subtask, taskManager.getSubtask(3));
    }

    @Test
    public void TasksWithGeneratedIdAndGivenIdShouldNotConflict() {
        Task task1 = new Task(TaskStatus.NEW, "description", "name");
        taskManager.addNewTask(task1);
        Task task2 = new Task(TaskStatus.DONE, "Other description", "other name");
        task2.setId(2);
        taskManager.addNewTask(task2);
        assertNotNull(taskManager.getTask(2));
    }

    @Test
    public void TaskShouldNotChangeAfterAdding() {
        Task task = new Task(TaskStatus.NEW, "description", "name");
        taskManager.addNewTask(task);
        Task taskCloneAfterAdding = taskManager.getTask(1);
        assertEquals(task.getName(), taskCloneAfterAdding.getName());
        assertEquals(task.getStatus(), taskCloneAfterAdding.getStatus());
        assertEquals(task.getDescription(), taskCloneAfterAdding.getDescription());
    }

    @Test
    public void HistoryManagerShouldSavePreviousVersionOfTask() {
        Task task = new Task(TaskStatus.NEW, "description", "name");
        taskManager.addNewTask(task);
        taskManager.getTask(1);
        List<Task> tasksInHistory = taskManager.getHistory();
        assertTrue(tasksInHistory.contains(task));
    }

}