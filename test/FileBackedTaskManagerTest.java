import manager.FileBackedTaskManager;
import manager.exceptions.TaskOverlapException;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndLoadFromFile() throws TaskOverlapException {
        Task task = new Task(TaskStatus.NEW, "Task", "Desc", 30, LocalDateTime.now());
        taskManager.addNewTask(task);
        int taskId = task.getId();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(task, loaded.getTask(taskId));
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.deleteOnExit();
        }
    }
}