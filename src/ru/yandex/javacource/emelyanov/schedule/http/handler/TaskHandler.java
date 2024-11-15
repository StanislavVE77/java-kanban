package ru.yandex.javacource.emelyanov.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.TaskValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case POST_NEW_TASK:
                handlePostNewTask(httpExchange);
                break;
            case POST_UPDATE_TASK:
                handlePostUpdateTask(httpExchange);
                break;
            case GET_TASKS:
                handleGetTasks(httpExchange);
                break;
            case GET_TASK:
                handleGetTaskById(httpExchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(httpExchange);
                break;
            default:
                sendTextResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
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
            sendTextResponse(exchange, "Задача удалена.", 204);
        }
    }

    private void handlePostUpdateTask(HttpExchange exchange) throws IOException {
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
            Task task = gson.fromJson(body, Task.class);
            task.setId(id);
            try {
                taskManager.updateTask(task);
                sendTextResponse(exchange, "Задача обновлена.", 201);
            } catch (NullPointerException exception) {
                sendNotFound(exchange, "Задача не обновлена.");
            } catch (TaskValidationException exception) {
                sendHasInteractions(exchange, "Задача пересекается по времени с ранее созданной.");
            }
        }
    }

    private void handlePostNewTask(HttpExchange exchange) throws IOException {
        try (exchange) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            try {
                if (task.getDuration() == null) {
                    task.setDuration(Duration.ofMinutes(15));
                }
                if (task.getStartTime() == null) {
                    task.setStartTime(LocalDateTime.now());
                }
                taskManager.createTask(task);
                sendTextResponse(exchange, "Задача создана. id = " + task.getId(), 201);
            } catch (NullPointerException exception) {
                sendNotFound(exchange, "Задача не создана.");
            } catch (TaskValidationException exception) {
                sendHasInteractions(exchange, "Задача пересекается по времени с ранее созданной.");
            }
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int id;
            try {
                id = Integer.parseInt(pathParts[2]);
                Task currentTask = taskManager.getTask(id);
            } catch (NumberFormatException | NullPointerException exception) {
                sendNotFound(exchange, "Задача с идентификатором " + pathParts[2] + " не найдена.");
                return;
            }
            String tasksJson = gson.toJson(taskManager.getTask(id));
            sendText(exchange, tasksJson);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try (exchange) {
            String tasksJson = gson.toJson(taskManager.getAllTasks());
            sendText(exchange, tasksJson);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_NEW_TASK;
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATE_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_TASKS, GET_TASK, POST_NEW_TASK, POST_UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
