package manager.interfaces;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubtask(Subtask subtask);

    void updateTask(Task task, TaskStatus taskStatus);

    void updateSubtask(Subtask subtask, TaskStatus taskStatus);

    void deleteById(int id);

    Epic getEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);

    List<Task> getHistory();
}
