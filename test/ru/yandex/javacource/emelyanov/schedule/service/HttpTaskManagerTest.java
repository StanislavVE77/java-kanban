package ru.yandex.javacource.emelyanov.schedule.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.javacource.emelyanov.schedule.http.HttpTaskServer;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTask();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Проверка создания задачи.")
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Проверка изменения задачи.")
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now()));
        int id = task.getId();
        Task taskUpdate = new Task(id, "Test 1 UPDATE", TaskStatus.NEW, "Testing task 1 UPDATE", Duration.ofMinutes(10), LocalDateTime.now());

        String taskJson = gson.toJson(taskUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1 UPDATE", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Проверка удаления задачи.")
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now()));
        int id = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Задача не удалена.");
    }

    @Test
    @DisplayName("Проверка получения списка задач.")
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        int id1 = task1.getId();
        Task task2 = manager.createTask(new Task("Test 2", TaskStatus.NEW, "Testing task 2", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(10))));
        int id2 = task2.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String testString = "[\n" +
                "  {\n" +
                "    \"name\": \"Test 1\",\n" +
                "    \"description\": \"Testing task 1\",\n" +
                "    \"id\": " + id1 + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:30:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Test 2\",\n" +
                "    \"description\": \"Testing task 2\",\n" +
                "    \"id\": " + id2 + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:20:00\"\n" +
                "  }\n" +
                "]";
        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(testString, response.body(), "Задачи не получены.");
    }

    @Test
    @DisplayName("Проверка получения задачи по id.")
    public void testGetTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        int id = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String testString = "{\n" +
                "  \"name\": \"Test 1\",\n" +
                "  \"description\": \"Testing task 1\",\n" +
                "  \"id\": " + id + ",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": 5,\n" +
                "  \"startTime\": \"2000-10-30 12:30:00\"\n" +
                "}";

        assertEquals(testString, response.body(), "Задача не получена.");
    }

    @Test
    @DisplayName("Проверка получения истории.")
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Task task2 = manager.createTask(new Task("Test 2", TaskStatus.NEW, "Testing task 2", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(10))));
        Task task3 = manager.createTask(new Task("Test 3", TaskStatus.NEW, "Testing task 3", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(20))));
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String testString = "[\n" +
                "  {\n" +
                "    \"name\": \"Test 2\",\n" +
                "    \"description\": \"Testing task 2\",\n" +
                "    \"id\": " + task2.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:20:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Test 3\",\n" +
                "    \"description\": \"Testing task 3\",\n" +
                "    \"id\": " + task3.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:10:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Test 1\",\n" +
                "    \"description\": \"Testing task 1\",\n" +
                "    \"id\": " + task1.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:30:00\"\n" +
                "  }\n" +
                "]";

        assertEquals(testString, response.body(), "История не получена.");
    }

    @Test
    @DisplayName("Проверка приоритетного списка задач.")
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Task task2 = manager.createTask(new Task("Test 2", TaskStatus.NEW, "Testing task 2", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(10))));
        Task task3 = manager.createTask(new Task("Test 3", TaskStatus.NEW, "Testing task 3", Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(20))));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String testString = "[\n" +
                "  {\n" +
                "    \"name\": \"Test 3\",\n" +
                "    \"description\": \"Testing task 3\",\n" +
                "    \"id\": " + task3.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:10:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Test 2\",\n" +
                "    \"description\": \"Testing task 2\",\n" +
                "    \"id\": " + task2.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:20:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"Test 1\",\n" +
                "    \"description\": \"Testing task 1\",\n" +
                "    \"id\": " + task1.getId() + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:30:00\"\n" +
                "  }\n" +
                "]";

        assertEquals(testString, response.body(), "Список приоритетных задач не корректный.");
    }

    @Test
    @DisplayName("Проверка создания эпика.")
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", TaskStatus.NEW, "Testing task 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Проверка удаления эпика.")
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Test 1", TaskStatus.NEW, "Testing task 1"));
        int id = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(0, tasksFromManager.size(), "Эпик не удален.");
    }

    @Test
    @DisplayName("Проверка получения списка подзадач эпика.")
    public void testGetAllEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Test 1", TaskStatus.NEW, "Testing epic 1"));
        int id = epic.getId();
        Subtask subtask1 = manager.createSubtask(new Subtask("Test 2", TaskStatus.NEW, "Testing subtask 1", id, Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        int id1 = subtask1.getId();
        Subtask subtask2 = manager.createSubtask(new Subtask("Test 3", TaskStatus.NEW, "Testing subtask 2", id, Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0).minus(Duration.ofMinutes(10))));
        int id2 = subtask2.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/" + id + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String testString = "[\n" +
                "  {\n" +
                "    \"epicId\": 1,\n" +
                "    \"name\": \"Test 2\",\n" +
                "    \"description\": \"Testing subtask 1\",\n" +
                "    \"id\": " + id1 + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:30:00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"epicId\": 1,\n" +
                "    \"name\": \"Test 3\",\n" +
                "    \"description\": \"Testing subtask 2\",\n" +
                "    \"id\": " + id2 + ",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2000-10-30 12:20:00\"\n" +
                "  }\n" +
                "]";

        assertEquals(testString, response.body(), "Эпики не получены.");
    }

    @Test
    @DisplayName("Проверка создания подзадачи.")
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Test 1", TaskStatus.NEW, "Testing epic 1"));
        int id = epic.getId();
        Subtask subtask = new Subtask("Test 2", TaskStatus.NEW, "Testing subtask 1", id, Duration.ofMinutes(5), LocalDateTime.of(2000, 10, 30, 12, 30, 0));

        String epicJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

}