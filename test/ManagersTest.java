import Manager.Interfaces.HistoryManager;
import Manager.Interfaces.TaskManager;
import Manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class ManagersTest {

    @Test
    public void AllManagersShouldBeCreatedInitialized() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertEquals(new HashMap<>(), taskManager.getTasks());
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertEquals(new ArrayList<>(), historyManager.getHistory());
    }

}