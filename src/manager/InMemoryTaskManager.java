package manager;

import manager.exceptions.TaskOverlapException;
import manager.interfaces.HistoryManager;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime));
    private int id = 0;

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
    public void addNewTask(Task task) throws TaskOverlapException {
        if (task != null) {
            if (isTaskOverlappingAnyExisting(task)) {
                setTaskId(task);
                tasks.put(id, task);
                if (task.getStartTime() != null) {
                    prioritizedTasks.add(task);
                }
                System.out.println("Задача добавлена под номером " + id);
            } else {
                throw new TaskOverlapException("Ошибка:пересечение времени выполнения задач.");
            }
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void addNewEpic(Epic epic) throws TaskOverlapException {
        if (epic != null) {
            if (isTaskOverlappingAnyExisting(epic)) {
                setTaskId(epic);
                epics.put(id, epic);
                if (epic.getStartTime() != null) {
                    prioritizedTasks.add(epic);
                }
                System.out.println("Задача добавлена под номером " + id);
            } else {
                throw new TaskOverlapException("Ошибка:пересечение времени выполнения задач.");
            }
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) throws TaskOverlapException {
        if (subtask != null) {
            if (isTaskOverlappingAnyExisting(subtask)) {
                setTaskId(subtask);
                subtasks.put(id, subtask);
                if (subtask.getStartTime() != null) {
                    prioritizedTasks.add(subtask);
                }
                System.out.println("Задача добавлена под номером " + id);
            } else {
                throw new TaskOverlapException("Ошибка:пересечение времени выполнения задач.");
            }
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void updateTask(Task task, int oldTaskId) throws TaskOverlapException {
        if (task != null) {
            if (isTaskOverlappingAnyExisting(task)) {
                tasks.put(oldTaskId, task);
                System.out.println("Задача успешно обновлена");
            } else {
                throw new TaskOverlapException("Ошибка:пересечение времени выполнения задач.");
            }
        } else {
            System.out.println("Ошибка: задача не может быть null");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, int oldTaskId) throws TaskOverlapException {
        if (subtask != null) {
            if (isTaskOverlappingAnyExisting(subtask)) {
                subtasks.put(oldTaskId, subtask);
                subtask.getParentEpic().updateSubtask(subtask);
                System.out.println("Задача успешно обновлена");
            } else {
                throw new TaskOverlapException("Ошибка:пересечение времени выполнения задач.");
            }
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(Duration.ofMinutes(task1.getDuration()));
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(Duration.ofMinutes(task2.getDuration()));

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Override
    public boolean isTaskOverlappingAnyExisting(Task newTask) {
        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .noneMatch(existingTask -> isTasksOverlap(existingTask, newTask));
    }

    public void setTaskId(Task task) {
        if (task.getId() > id) {
            id = task.getId();
        } else {
            id++;
            task.setId(id);
        }
    }


}
