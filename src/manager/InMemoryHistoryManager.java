package manager;

import manager.interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history = new HashMap<>();
    private Node tail;
    private Node head;

    @Override
    public void add(Task task) {
        if (task == null){
            return;
        }
        int id = task.getId();

        remove(id);

        Node newNode = new Node(task);
        linkLast(newNode);
        history.put(id, newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        Node currentNode = head;
        while (true) {
            if (currentNode == null) {
                break;
            }
            historyTasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return historyTasks;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }
}
