package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(TaskStatus status, String name, String description, int duration, LocalDateTime startTime) {
        this.status = status;
        this.description = description;
        this.name = name;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public static DateTimeFormatter getFormat() {
        return DATE_TIME_FORMATTER;
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

    public int getDuration() {
        return (int) duration.toMinutes();
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeFormatted() {
        return startTime.format(DATE_TIME_FORMATTER);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", id, getType().name(), name,
                status, description, getDuration(), getStartTimeFormatted(),
                getEndTime().format(DATE_TIME_FORMATTER));
    }


    @Override
    public int compareTo(Task o) {
        if (!(o instanceof Task task)) {
            return -1;
        }
        return getId() - task.getId();
    }
}
