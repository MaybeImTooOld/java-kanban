package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.TaskOverlapException;
import manager.interfaces.TaskManager;
import model.Epic;
import server.Serializator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        if (!path.startsWith("/epics")) {
            sendNotFound(exchange);
            return;
        }

        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        methodGetEpics(exchange);
                    } else if (pathParts.length == 3) {
                        methodGetEpicByID(exchange, pathParts[2]);
                    } else if (pathParts.length == 4) {
                        methodGetEpicsSubtasks(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2) {
                        methodPostNewEpic(exchange);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        methodDeleteEpic(exchange, pathParts[2]);
                    }
                    break;
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void methodGetEpics(HttpExchange exchange) throws IOException {
        Map<Integer, Epic> epics = manager.getEpics();
        String response = Serializator.gsonForTasks.toJson(epics);
        sendResponse(exchange, 200, response);
    }

    private void methodGetEpicByID(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Epic epic = manager.getEpic(id);

            if (epic == null) {
                sendNotFound(exchange);
                return;
            }

            String response = Serializator.gsonForTasks.toJson(epic);
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid epic ID format");
        }
    }

    private void methodPostNewEpic(HttpExchange exchange) throws IOException {
        try {
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            Epic epic = Serializator.gsonForTasks.fromJson(body, Epic.class);

            manager.addNewEpic(epic);
            sendResponse(exchange, 201, "Adding epic succeed");
        } catch (TaskOverlapException e) {
            sendNotAcceptable(exchange);
        }
    }

    private void methodGetEpicsSubtasks(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Epic epic = manager.getEpic(id);

            if (epic == null) {
                sendNotFound(exchange);
                return;
            }

            String response = Serializator.gsonForTasks.toJson(epic.getSubtasks());
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid epic ID format");
        }
    }

    private void methodDeleteEpic(HttpExchange exchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            manager.deleteById(id);
            sendResponse(exchange, 200, "Deleted success");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Invalid epic ID format");
        }

    }
}
