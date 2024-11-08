package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    static File file = new File("tasks_test.csv");
    //TaskManager tm;

    @Override
    public FileBackedTaskManager createManager() {
        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        return fileTaskManager;
    }

    @Test
    @DisplayName("Чтение из пустого файла")
    void shouldLoadFromEmptyFile() {

        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        fileTaskManager.deleteAllTask();
        TaskManager taskManager = fileTaskManager.loadFromFile(file);

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    @DisplayName("Запись в  пустой файл")
    void shouldSaveToEmptyFile() {

        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        fileTaskManager.deleteAllTask();

        Task task1 = fileTaskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание 1", Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T10:00:00")));
        Task task2 = fileTaskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание 2", Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T11:00:00")));
        Epic epic1 = fileTaskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание 2"));
        Subtask subtask1 = fileTaskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T12:00:00")));
        Subtask subtask2 = fileTaskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T13:00:00")));
        final List<Task> tasks = fileTaskManager.getAllTasks();
        final List<Epic> epics = fileTaskManager.getAllEpics();
        final List<Subtask> subtasks = fileTaskManager.getAllSubtasks();
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                lineCount++;
            }
        } catch (IOException exception) {
            throw new FileException("Ошибка записи в файл: " + file.getAbsolutePath(), exception);
        }

        assertEquals(lineCount, tasks.size() + epics.size() + subtasks.size(), "Неверное количество задач записалось в файл.");
    }

    @Test
    @DisplayName("Чтение задач из непустого файла")
    void shouldLoadFromNotEmptyFile() {
        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        fileTaskManager.deleteAllTask();

        Task task1 = fileTaskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание 1", Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T10:00:00")));
        Task task2 = fileTaskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание 2", Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T11:00:00")));
        Epic epic1 = fileTaskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание 2"));
        Subtask subtask1 = fileTaskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T13:00:00")));
        Subtask subtask2 = fileTaskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T14:00:00")));

        TaskManager taskManager = fileTaskManager.loadFromFile(file);

        final List<Task> tasks = taskManager.getAllTasks();
        final List<Epic> epics = fileTaskManager.getAllEpics();
        final List<Subtask> subtasks = fileTaskManager.getAllSubtasks();

        assertEquals(5, tasks.size() + epics.size() + subtasks.size(), "Неверное количество задач загружено из файл.");
    }

    @Test
    @DisplayName("Проверка исключительной ситуации")
    public void testException() {
        assertThrows(FileNotFoundException.class, () -> {
            BufferedReader reader = new BufferedReader(new FileReader(new File("tasks_error.csv")));
        }, "Отсутствие файла должно приводить к исключению");
    }
}
