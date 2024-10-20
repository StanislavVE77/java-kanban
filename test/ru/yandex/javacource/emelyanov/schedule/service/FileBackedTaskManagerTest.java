package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Task task1 = fileTaskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание 1"));
        Task task2 = fileTaskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание 2"));
        Epic epic1 = fileTaskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание 2"));
        Subtask subtask1 = fileTaskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId()));
        Subtask subtask2 = fileTaskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId()));
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

        Task task1 = fileTaskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание 1"));
        Task task2 = fileTaskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание 2"));
        Epic epic1 = fileTaskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание 2"));
        Subtask subtask1 = fileTaskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId()));
        Subtask subtask2 = fileTaskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId()));

        TaskManager taskManager = fileTaskManager.loadFromFile(file);

        final List<Task> tasks = taskManager.getAllTasks();
        final List<Epic> epics = fileTaskManager.getAllEpics();
        final List<Subtask> subtasks = fileTaskManager.getAllSubtasks();

        assertEquals(5, tasks.size() + epics.size() + subtasks.size(), "Неверное количество задач загружено из файл.");
    }

}
