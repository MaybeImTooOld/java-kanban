package manager.interfaces;

import manager.exceptions.TaskOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {
    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    void addNewTask(Task task) throws TaskOverlapException;

    void addNewEpic(Epic epic) throws TaskOverlapException;

    void addNewSubtask(Subtask subtask) throws TaskOverlapException;

    void updateTask(Task task, TaskStatus taskStatus) throws TaskOverlapException;

    void updateSubtask(Subtask subtask, TaskStatus taskStatus) throws TaskOverlapException;

    void deleteById(int id);

    Epic getEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);

    List<Task> getHistory();

    boolean isTaskOverlappingAnyExisting(Task newTask);

    Set<Task> getPrioritizedTasks();
}
