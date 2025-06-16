package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.TaskOverlapException;
import manager.interfaces.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        if (!path.startsWith("/tasks")) {
            sendNotFound(exchange);
            return;
        }

        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        methodGetTasks(exchange);
                    } else if (pathParts.length == 3) {
                        methodGetTaskByID(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2) {
                        methodPostNewTask(exchange);
                    } else if (pathParts.length == 3) {
                        methodPostUpdateTask(exchange, pathParts[2]);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        methodDeleteTask(exchange, pathParts[2]);
                    }
                    break;
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void methodGetTasks(HttpExchange exchange) throws IOException {
        Map<Integer, Task> tasks = manager.getTasks();
        String response = gson.toJson(tasks);
        sendResponse(exchange, 200, response);
    }

    private void methodGetTaskByID(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Task task = manager.getTask(id);

            if (task == null) {
                sendNotFound(exchange);
                return;
            }

            String response = gson.toJson(task);
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid task ID format");
        }
    }

    private void methodPostNewTask(HttpExchange exchange) throws IOException {
        try {
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            Task task = gson.fromJson(body, Task.class);
            manager.addNewTask(task);
            sendResponse(exchange, 201, "Adding task succeed");
        } catch (TaskOverlapException e) {
            sendNotAcceptable(exchange);
        }

    }

    private void methodPostUpdateTask(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Task taskFromId = manager.getTask(id);

            if (taskFromId == null) {
                sendNotFound(exchange);
                return;
            }
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            Task task = gson.fromJson(body, Task.class);

            manager.updateTask(task, id);
            sendResponse(exchange, 201, "Update task succeed");
        } catch (TaskOverlapException e) {
            sendNotAcceptable(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid task ID format");
        }
    }

    private void methodDeleteTask(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            manager.deleteById(id);
            sendResponse(exchange, 200, "Deleted success");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid task ID format");
        }

    }
}