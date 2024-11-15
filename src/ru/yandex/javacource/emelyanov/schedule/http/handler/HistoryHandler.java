package ru.yandex.javacource.emelyanov.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY:
                handleGetHistory(httpExchange);
                break;
            default:
                sendTextResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try (exchange) {
            String tasksJson = gson.toJson(taskManager.getHistory());
            sendText(exchange, tasksJson);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            } else {
                return Endpoint.UNKNOWN;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_HISTORY, UNKNOWN}
}

