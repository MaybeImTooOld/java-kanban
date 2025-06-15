package manager;

import manager.exceptions.ManagerSaveException;
import manager.exceptions.TaskOverlapException;
import model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File tasksFile;

    public FileBackedTaskManager(File file) {
        this.tasksFile = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String buffer = br.readLine();
                if (buffer.isBlank()) {
                    break;
                }
                Task task = fb.fromString(buffer);
                switch (task.getType()) {
                    case TASK:
                        fb.addNewTask(task);
                        break;
                    case EPIC:
                        fb.addNewEpic((Epic) task);
                        break;
                    case SUBTASK:
                        fb.addNewSubtask((Subtask) task);
                        break;
                    default:
                        System.out.println("Тип " + task.getType() + " не поддерживается.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла " + e.getMessage());
        }
        return fb;
    }

    @Override
    public void addNewTask(Task task) throws TaskOverlapException {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) throws TaskOverlapException {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) throws TaskOverlapException {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task, TaskStatus taskStatus) throws TaskOverlapException {
        super.updateTask(task, taskStatus);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, TaskStatus taskStatus) throws TaskOverlapException {
        super.updateSubtask(subtask, taskStatus);
        save();
    }

    public void save() {
        Set<Task> sortedTasks = new TreeSet<>(getTasks().values());
        sortedTasks.addAll(getEpics().values());
        sortedTasks.addAll(getSubtasks().values());

        if (sortedTasks.isEmpty()) {
            return;
        }

        try (FileWriter fw = new FileWriter(tasksFile)) {
            fw.write("id,type,name,status,description,duration,startTime,endTime,epic\n");
            sortedTasks
                    .forEach(task -> {
                        try {
                            fw.write(task.toString() + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка записи файла!" + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла!" + e.getMessage());
        }
    }

    public Task fromString(String value) {
        String[] taskInfo = value.split(",");
        int id = Integer.parseInt(taskInfo[0]);
        TaskTypes type = TaskTypes.valueOf(taskInfo[1]);
        String name = taskInfo[2];
        TaskStatus status = TaskStatus.valueOf(taskInfo[3]);
        String description = taskInfo[4];
        int duration = Integer.parseInt(taskInfo[5]);
        LocalDateTime startTime = LocalDateTime.parse(taskInfo[6], Task.getFormat());
        switch (type) {
            case TASK:
                Task task = new Task(status, description, name, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(status, description, name, duration, startTime);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(taskInfo[8]);
                Epic epicBuffer = getEpic(epicId);
                return new Subtask(status, description, name, duration, startTime, epicBuffer);
        }
        return null;
    }

}
