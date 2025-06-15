package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Epic extends Task {
    Collection<Subtask> subtasks = new ArrayList<>();

    public Epic(TaskStatus status, String description, String name, int duration, LocalDateTime startTime) {

        super(status, description, name, duration, startTime);
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
        boolean hasInProgress = subtasks.stream()
                .map(Subtask::getStatus)
                .anyMatch(TaskStatus.IN_PROGRESS::equals);

        if (hasInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            long doneCount = subtasks.stream()
                    .map(Subtask::getStatus)
                    .filter(TaskStatus.DONE::equals)
                    .count();

            if (doneCount == subtasks.size()) {
                setStatus(TaskStatus.DONE);
            }
        }
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }
}
