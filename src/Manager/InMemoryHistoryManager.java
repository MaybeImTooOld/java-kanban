package Manager;

import Manager.Interfaces.HistoryManager;
import Model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();
    private static final int historySize = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= historySize ) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
