import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(TaskStatus status, String description, String name) {
        super(status, description, name);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.add(subtask);
        }
    }

    public void removeSubtask(Subtask subtask) {
        if (subtask != null) {

        }
    }
}
