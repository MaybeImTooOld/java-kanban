import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic(TaskStatus.NEW, "Task 2", "Desc", 45, LocalDateTime.now().plusHours(1));
    }

    @Test
    void shouldHaveNewStatusWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldHaveNewStatusWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.NEW, "Subtask 2", "Desc", 45, null, epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldHaveDoneStatusWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask(TaskStatus.DONE, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "Subtask 2", "Desc", 45, null, epic);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldHaveInProgressStatusWhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.IN_PROGRESS, "Subtask 2", "Desc", 45, null, epic);


        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldHaveInProgressStatusWhenAnySubtaskInProgress() {
        Subtask subtask1 = new Subtask(TaskStatus.IN_PROGRESS, "Subtask 1", "Desc", 30, null, epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "Subtask 2", "Desc", 45, null, epic);


        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(TaskStatus.NEW, "Task", "Desc", 30, LocalDateTime.now());
        Epic epic2 = new Epic(TaskStatus.NEW, "Task", "Desc", 30, LocalDateTime.now().minusDays(1));
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void shouldUpdateStatusWhenSubtaskChanges() {
        Subtask subtask = new Subtask(TaskStatus.NEW, "Subtask 1", "Desc", 30, null, epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        epic.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        subtask.setStatus(TaskStatus.DONE);
        epic.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }
}
