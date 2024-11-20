package ru.yandex.javacource.emelyanov.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIOR:
                handleGetPrioritized(httpExchange);
                break;
            default:
                sendTextResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        try (exchange) {
            String tasksJson = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, tasksJson);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIOR;
            } else {
                return Endpoint.UNKNOWN;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_PRIOR,
        UNKNOWN
    }
}