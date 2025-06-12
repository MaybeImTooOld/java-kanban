package Manager;

import Manager.Interfaces.HistoryManager;
import Manager.Interfaces.TaskManager;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            id++;
            task.setId(id);
            tasks.put(id, task);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            id++;
            epic.setId(id);
            epics.put(id, epic);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            id++;
            subtask.setId(id);
            subtasks.put(id, subtask);
            System.out.println("Задача добавлена под номером " + id);
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void updateTask(Task task, TaskStatus taskStatus) {
        if (task != null) {
            task.setStatus(taskStatus);
            tasks.put(task.getId(), task);
            System.out.println("Задача успешно обновлена");
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, TaskStatus taskStatus) {
        if (subtask != null) {
            subtask.setStatus(taskStatus);
            subtasks.put(subtask.getId(), subtask);
            subtask.getParentEpic().updateSubtask(subtask);
            System.out.println("Задача успешно обновлена");
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void deleteById(int id) {
        tasks.remove(id);
        epics.remove(id);
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            return null;
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}
