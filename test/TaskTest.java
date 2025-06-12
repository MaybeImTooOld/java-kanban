import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EqualsByIdTest {

    @Test
    public void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(TaskStatus.NEW, "description", "name");
        Task task2 = new Task(TaskStatus.DONE, "Other description", "other name");
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(TaskStatus.NEW, "description", "name");
        Epic epic2 = new Epic(TaskStatus.DONE, "Other description", "other name");
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void subtasksWithSameIdShouldBeEqual() {
        Epic epic = new Epic(TaskStatus.NEW, "Parent", "Parent");
        Subtask subtask1 = new Subtask(TaskStatus.NEW, "description", "name", epic);
        Subtask subtask2 = new Subtask(TaskStatus.DONE, "Other description", "other name", epic);
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(subtask1, subtask2);
    }

}