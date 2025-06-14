package model;

import java.util.Objects;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    public Task(TaskStatus status, String description, String name) {
        this.status = status;
        this.description = description;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, getType().name(), name, status, description);
    }


    @Override
    public int compareTo(Task o) {
        if (!(o instanceof Task task)) {
            return -1;
        }
        return getId() - task.getId();
    }
}
