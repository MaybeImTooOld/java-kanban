package Model;

public class Subtask extends Task{

    private final Epic parentEpic;

    public Subtask(TaskStatus status, String description, String name , Epic parentEpic) {
        super(status, description, name);
        this.parentEpic = parentEpic;
    }

    public Epic getParentEpic() {
        return parentEpic;
    }

    @Override
    public String toString() {
        return "Model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
