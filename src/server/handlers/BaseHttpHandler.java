package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {


    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        exchange.sendResponseHeaders(404, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = "Bad Request: " + message;
        exchange.sendResponseHeaders(400, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        String response = "Not acceptable";
        exchange.sendResponseHeaders(406, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        String response = "Internal Server Error";
        exchange.sendResponseHeaders(500, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
