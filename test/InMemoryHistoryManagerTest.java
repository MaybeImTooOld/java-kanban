import Manager.Interfaces.HistoryManager;
import Manager.Interfaces.TaskManager;
import Manager.Managers;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    static TaskManager taskManager;

    @BeforeAll
    public static void CreateTaskManager(){
        taskManager = Managers.getDefault();
        Task task1 = new Task(TaskStatus.NEW , "description", "name");
        Task task2 = new Task(TaskStatus.DONE, "Other description", "other name");
        Task task3 = new Task(TaskStatus.NEW , "description", "name");
        Task task4 = new Task(TaskStatus.IN_PROGRESS, "Other description", "other name");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);
    }

    @Test
    public void OldIdsShouldNotBeKeptInManager() {
        taskManager.getTask(1);
        taskManager.getTask(4);
        taskManager.getTask(3);
        taskManager.getTask(2);
        List<Task> buffer = taskManager.getHistory();
        System.out.println(buffer);
        taskManager.deleteById(3);
        System.out.println(taskManager.getHistory());
        Assertions.assertNotEquals(buffer,taskManager.getHistory());

    }

}
