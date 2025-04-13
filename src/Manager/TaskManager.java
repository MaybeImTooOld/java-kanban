package Manager;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

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
