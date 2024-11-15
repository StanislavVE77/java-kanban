package ru.yandex.javacource.emelyanov.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.TaskValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoint) {
            case POST_NEW_SUBTASK:
                handlePostNewSubtask(httpExchange);
                break;
            case POST_UPDATE_SUBTASK:
                handlePostUpdateSubtask(httpExchange);
                break;
            case GET_SUBTASKS:
                handleGetSubtasks(httpExchange);
                break;
            case GET_SUBTASK:
                handleGetSubtaskById(httpExchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(httpExchange);
                break;
            default:
                sendTextResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
            } catch (NumberFormatException exception) {
                sendNotFound(exchange, "Идентификатор не корректный.");
                return;
            }
            taskManager.deleteTask(id);
            sendTextResponse(exchange, "Подзадача удалена.", 204);
        }
    }

    private void handlePostUpdateSubtask(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
            } catch (NumberFormatException exception) {
                sendNotFound(exchange, "Идентификатор не корректный.");
                return;
            }
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            subtask.setId(id);
            try {
                taskManager.updateSubtask(subtask);
                sendTextResponse(exchange, "Подзадача обновлена.", 201);
            } catch (NullPointerException exception) {
                sendNotFound(exchange, "Подзадача не обновлена.");
            } catch (TaskValidationException exception) {
                sendHasInteractions(exchange, "Подзадача пересекается по времени с ранее созданной.");
            }
        }
    }

    private void handlePostNewSubtask(HttpExchange exchange) throws IOException {
        try (exchange) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            try {
                if (subtask.getDuration() == null) {
                    subtask.setDuration(Duration.ofMinutes(15));
                }
                if (subtask.getStartTime() == null) {
                    subtask.setStartTime(LocalDateTime.now());
                }
                taskManager.createSubtask(subtask);
                sendTextResponse(exchange, "Подзадача создана. id = " + subtask.getId(), 201);
            } catch (NullPointerException exception) {
                sendNotFound(exchange, "Подзадача не создана.");
            } catch (TaskValidationException exception) {
                sendHasInteractions(exchange, "Подзадача пересекается по времени с ранее созданной.");
            }
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
                Task currentSubtask = taskManager.getSubtask(id);
            } catch (NumberFormatException | NullPointerException exception) {
                sendNotFound(exchange, "Подзадача с идентификатором " + pathParts[2] + " не найдена.");
                return;
            }
            String SubtaskJson = gson.toJson(taskManager.getSubtask(id));
            sendText(exchange, SubtaskJson);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try (exchange) {
            String subtasksJson = gson.toJson(taskManager.getAllSubtasks());
            sendText(exchange, subtasksJson);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_NEW_SUBTASK;
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATE_SUBTASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_SUBTASKS, GET_SUBTASK, POST_NEW_SUBTASK, POST_UPDATE_SUBTASK, DELETE_SUBTASK, UNKNOWN}

}
