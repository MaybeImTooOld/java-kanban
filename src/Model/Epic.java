package Model;

import java.util.ArrayList;
import java.util.Collection;

public class Epic extends Task {
    Collection<Subtask> subtasks = new ArrayList<>();

    public Epic(TaskStatus status, String description, String name) {
        super(status, description, name);
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks;
    }


    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.add(subtask);
            checkStatus();
        }
    }

    public void updateSubtask(Subtask subtaskNew) {
        subtasks.remove(subtaskNew);
        subtasks.add(subtaskNew);
        checkStatus();
    }

    public void removeSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.remove(subtask);
            checkStatus();
        }
    }

    public void checkStatus() {
        int count = 0;
        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case TaskStatus.IN_PROGRESS: {
                    setStatus(TaskStatus.IN_PROGRESS);
                    return;
                }
                case TaskStatus.DONE: {
                    count++;
                    break;
                }
            }
        }
        if (count == subtasks.size()) {
            setStatus(TaskStatus.DONE);
        }
    }
}
