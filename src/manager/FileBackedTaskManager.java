package manager;

import model.*;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File tasksFile;

    public FileBackedTaskManager(File file) {
        this.tasksFile = file;
    }


    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task, TaskStatus taskStatus) {
        super.updateTask(task, taskStatus);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, TaskStatus taskStatus) {
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
            fw.write("id,type,name,status,description,epic\n");
            for (Task task : sortedTasks) {
                fw.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        if (file == null) {
            System.out.println("Файл не найден");
            return fb;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                Task task = fb.fromString(br.readLine());
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
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла:" + e.getMessage());
        }
        return fb;
    }

    public Task fromString(String value) {
        String[] taskInfo = value.split(",");
        int id = Integer.parseInt(taskInfo[0]);
        TaskTypes type = TaskTypes.valueOf(taskInfo[1]);
        String name = taskInfo[2];
        TaskStatus status = TaskStatus.valueOf(taskInfo[3]);
        String description = taskInfo[4];
        switch (type) {
            case TASK:
                Task task = new Task(status, description, name);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(status, description, name);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(taskInfo[5]);
                Epic epicBuffer = getEpic(epicId);
                return new Subtask(status, description, name, epicBuffer);
        }
        return null;
    }

}
