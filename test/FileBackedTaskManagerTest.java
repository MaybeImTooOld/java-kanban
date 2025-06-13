import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fb;
    File file;

    @BeforeEach
    void fileBackedTaskManagerCreation() {
        try {
            file = File.createTempFile("Test", ".csv", new File("/Users/yee/almost everything"));
            fb = new FileBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void deletingTemp() {
        file.delete();
    }


    @Test
    void savingAndReadingEmptyFileTest() {
        fb.save();
        fb = FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    public void savingAndReadingFromFileTest() {
        Task task = new Task(TaskStatus.NEW, "description", "name");
        Epic epic = new Epic(TaskStatus.NEW, "description", "name");
        Subtask subtask = new Subtask(TaskStatus.NEW, "description", "name", epic);
        epic.addSubtask(subtask);
        fb.addNewTask(task);
        fb.addNewEpic(epic);
        fb.addNewSubtask(subtask);
        FileBackedTaskManager fbFromFile = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(fb.getTasks(), fbFromFile.getTasks());
        Assertions.assertEquals(fb.getSubtasks(), fbFromFile.getSubtasks());
    }
}
