package Manager;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private HashMap<Integer,Task> tasks = new HashMap<>();
    private HashMap<Integer,Epic> epics = new HashMap<>();
    private HashMap<Integer,Subtask> subtasks = new HashMap<>();

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addNewTask (Task task) {
        if (task != null) {
            id++;
            task.setId(id);
            tasks.put(id,task);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    public void addNewEpic (Epic epic) {
        if (epic != null) {
            id++;
            epic.setId(id);
            epics.put(id,epic);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    public void addNewSubtask (Subtask subtask) {
        if (subtask != null) {
            id++;
            subtask.setId(id);
            subtasks.put(id,subtask);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    public void updateTask (Task task, TaskStatus taskStatus) {
        if (task != null) {
            task.setStatus(taskStatus);
            tasks.put(task.getId(), task);
            System.out.println("Задача успешно обновлена");
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    public void updateSubtask (Subtask subtask,TaskStatus taskStatus) {
        if (subtask != null) {
            subtask.setStatus(taskStatus);
            subtasks.put(subtask.getId(), subtask);
            subtask.getParentEpic().updateSubtask(subtask);
            System.out.println("Задача успешно обновлена");
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    public void deleteById (int id) {
        tasks.remove(id);
        epics.remove(id);
        subtasks.remove(id);
    }

    public Task getById (int id) {
        if (tasks.containsKey(id)){
            return tasks.get(id);
        }
        if (epics.containsKey(id)){
            return epics.get(id);
        } else {
            return subtasks.get(id);
        }
    }

}
