package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.interfaces.TaskManager;
import server.Serializator;

import java.io.IOException;
import java.net.URI;

public class PrioritizedHandler extends BaseHttpHandler {
    TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        if (path.equals("/prioritized") && method.equals("GET")) {
            String response = Serializator.gsonForTasks.toJson(manager.getPrioritizedTasks());
            sendResponse(exchange, 200, response);
        } else {
            sendNotFound(exchange);
        }
    }
}
