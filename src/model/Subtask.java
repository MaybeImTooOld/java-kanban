package model;

public class Subtask extends Task {
    private final Epic parentEpic;

    public Subtask(TaskStatus status, String description, String name, Epic parentEpic) {
        super(status, description, name);
        this.parentEpic = parentEpic;
        parentEpic.addSubtask(this);
    }

    public Epic getParentEpic() {
        return parentEpic;
    }

    @Override
    public String toString() {
        if (parentEpic == null) {
            return super.toString();
        }
        return super.toString() + "," + parentEpic.getId();
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }
}
