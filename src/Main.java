import Manager.InMemoryTaskManager;
import Manager.Interfaces.TaskManager;
import Manager.Managers;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task1 = new Task(TaskStatus.NEW, "Тестируем таски1", "Таск1");
        Task task2 = new Task(TaskStatus.NEW, "Тестируем таски2", "Таск2");
        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        Epic epic1 = new Epic(TaskStatus.NEW, "Тестируем эпики1", "Эпик1");
        inMemoryTaskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски1","сабтаск1",epic1);
        inMemoryTaskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски2","сабтаск2",epic1);
        inMemoryTaskManager.addNewSubtask(subtask2);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        Epic epic2 = new Epic(TaskStatus.NEW, "Тестируем эпики2", "Эпик2");
        inMemoryTaskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски3","сабтаск3",epic2);
        epic2.addSubtask(subtask3);
        inMemoryTaskManager.addNewSubtask(subtask3);
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.updateTask(task1,TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task2,TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1,TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask2,TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask3,TaskStatus.IN_PROGRESS);
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        System.out.println(inMemoryTaskManager.getHistory());
    }
}
