package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.TaskOverlapException;
import manager.interfaces.TaskManager;
import model.Subtask;
import server.Serializator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        if (!path.startsWith("/subtasks")) {
            sendNotFound(exchange);
            return;
        }

        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        methodGetSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        methodGetSubtaskByID(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2) {
                        methodPostNewSubtask(exchange);
                    } else if (pathParts.length == 3) {
                        methodPostUpdateSubtask(exchange, pathParts[2]);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        methodDeleteSubtask(exchange, pathParts[2]);
                    }
                    break;
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void methodGetSubtasks(HttpExchange exchange) throws IOException {
        Map<Integer, Subtask> subtasks = manager.getSubtasks();
        String response = Serializator.gsonForTasks.toJson(subtasks);
        sendResponse(exchange, 200, response);
    }

    private void methodGetSubtaskByID(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Subtask subtask = manager.getSubtask(id);

            if (subtask == null) {
                sendNotFound(exchange);
                return;
            }

            String response = Serializator.gsonForTasks.toJson(subtask);
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid subtask ID format");
        }
    }

    private void methodPostNewSubtask(HttpExchange exchange) throws IOException {
        try {
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            Subtask subtask = Serializator.gsonForTasks.fromJson(body, Subtask.class);

            manager.addNewSubtask(subtask);
            sendResponse(exchange, 201, "Adding subtask succeed");
        } catch (TaskOverlapException e) {
            sendNotAcceptable(exchange);
        }

    }

    private void methodPostUpdateSubtask(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Subtask subtaskFromId = manager.getSubtask(id);

            if (subtaskFromId == null) {
                sendNotFound(exchange);
                return;
            }
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            Subtask subtask = Serializator.gsonForTasks.fromJson(body, Subtask.class);

            manager.updateSubtask(subtask, id);
            sendResponse(exchange, 201, "Update subtask succeed");
        } catch (TaskOverlapException e) {
            sendNotAcceptable(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid subtask ID format");
        }
    }

    private void methodDeleteSubtask(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            manager.deleteById(id);
            sendResponse(exchange, 200, "Deleted success");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid subtask ID format");
        }

    }
}
