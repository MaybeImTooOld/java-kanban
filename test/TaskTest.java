import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TaskTest {

    @Test
    public void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(TaskStatus.NEW, "Task", "Desc", 30, LocalDateTime.now());
        Task task2 = new Task(TaskStatus.NEW, "Task", "Desc", 30, LocalDateTime.now().minusDays(1));
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void subtasksWithSameIdShouldBeEqual() {
        Epic epic = new Epic(TaskStatus.NEW, "Parent", "Desc", 30, LocalDateTime.now());
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "son", "Desc", 30, LocalDateTime.now().minusDays(2), epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "daughter", "Desc", 30, LocalDateTime.now().plusDays(3), epic);
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(subtask1, subtask2);
    }

}