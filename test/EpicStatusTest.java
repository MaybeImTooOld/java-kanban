import manager.InMemoryTaskManager;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Или другая реализация
        epic = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
        taskManager.addNewEpic(epic);
    }

    @Test
    void shouldHaveNewStatusWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldHaveNewStatusWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.NEW, "Subtask 2", "Desc", 45, null, epic);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldHaveDoneStatusWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask(TaskStatus.DONE, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "Subtask 2", "Desc", 45, null, epic);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldHaveInProgressStatusWhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.IN_PROGRESS, "Subtask 2", "Desc", 45, null, epic);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldHaveInProgressStatusWhenAnySubtaskInProgress() {
        Subtask subtask1 = new Subtask(TaskStatus.IN_PROGRESS, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "Subtask 2", "Desc", 45, null, epic);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldUpdateStatusWhenSubtaskChanges() {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateSubtask(subtask, TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        taskManager.updateSubtask(subtask, TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }
}
