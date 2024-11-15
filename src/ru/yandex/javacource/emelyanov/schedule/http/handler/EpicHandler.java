package ru.yandex.javacource.emelyanov.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.TaskValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case POST_NEW_EPIC:
                handlePostNewEpic(httpExchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(httpExchange);
                break;
            case GET_EPICS:
                handleGetEpics(httpExchange);
                break;
            case GET_EPIC:
                handleGetEpicById(httpExchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(httpExchange);
                break;
            default:
                sendTextResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
            } catch (NumberFormatException exception) {
                sendNotFound(exchange, "Идентификатор не корректный.");
                return;
            }
            taskManager.deleteEpic(id);
            sendTextResponse(exchange, "Эпик удален.", 204);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
                Epic currentEpic = taskManager.getEpic(id);
            } catch (NumberFormatException | NullPointerException exception) {
                sendNotFound(exchange, "Эпик с идентификатором " + pathParts[2] + " не найден.");
                return;
            }
            List<Subtask> epicSubtasks = new ArrayList<>();
            for (int sid : taskManager.getEpic(id).getSubTasks()) {
                epicSubtasks.add(taskManager.getSubtask(sid));
            }
            String epicSubtasksJson = gson.toJson(epicSubtasks);
            sendText(exchange, epicSubtasksJson);
        }
    }

    private void handlePostNewEpic(HttpExchange exchange) throws IOException {
        try (exchange) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            try {
                Epic newEpic = taskManager.createEpic(new Epic(epic.getName(), epic.getStatus(), epic.getDescription()));
                sendTextResponse(exchange, "Эпик создан. id = " + newEpic.getId(), 201);
            } catch (NullPointerException exception) {
                sendNotFound(exchange, "Эпик не создан.");
            } catch (TaskValidationException exception) {
                sendHasInteractions(exchange, "Эпик пересекается по времени с ранее созданным.");
            }
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
                Epic currentEpic = taskManager.getEpic(id);
            } catch (NumberFormatException | NullPointerException exception) {
                sendNotFound(exchange, "Эпик с идентификатором " + pathParts[2] + " не найден.");
                return;
            }
            String epicJson = gson.toJson(taskManager.getEpic(id));
            sendText(exchange, epicJson);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        try (exchange) {
            String epicsJson = gson.toJson(taskManager.getAllEpics());
            sendText(exchange, epicsJson);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_NEW_EPIC;
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        } else if (pathParts.length == 4) {
            if (requestMethod.equals("GET") && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_EPICS,
        GET_EPIC,
        POST_NEW_EPIC,
        GET_EPIC_SUBTASKS,
        DELETE_EPIC,
        UNKNOWN
    }
}