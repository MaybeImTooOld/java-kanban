import manager.interfaces.HistoryManager;
import manager.interfaces.TaskManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class ManagersTest {

    @Test
    public void allManagersShouldBeCreatedInitialized() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertEquals(new HashMap<>(), taskManager.getTasks());
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertEquals(new ArrayList<>(), historyManager.getHistory());
    }

}