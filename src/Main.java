import Manager.TaskManager;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task(TaskStatus.NEW, "Тестируем таски1", "Таск1");
        Task task2 = new Task(TaskStatus.NEW, "Тестируем таски2", "Таск2");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Epic epic1 = new Epic(TaskStatus.NEW, "Тестируем эпики1", "Эпик1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски1","сабтаск1",epic1);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски2","сабтаск2",epic1);
        taskManager.addNewSubtask(subtask2);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        Epic epic2 = new Epic(TaskStatus.NEW, "Тестируем эпики2", "Эпик2");
        taskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask(TaskStatus.NEW,"Тестируем сабтаски3","сабтаск3",epic2);
        epic2.addSubtask(subtask3);
        taskManager.addNewSubtask(subtask3);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        taskManager.updateTask(task1,TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2,TaskStatus.DONE);
        taskManager.updateSubtask(subtask1,TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2,TaskStatus.DONE);
        taskManager.updateSubtask(subtask3,TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}
