package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    Task task;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task = taskManager.createTask(new Task("Задача", TaskStatus.NEW, "Описание", Duration.ofMinutes(8), LocalDateTime.parse("2020-01-11T10:00:00")));
    }

    @Test
    @DisplayName("Утилитарный класс должен возвращать проинициализированные и готовые к работе экземпляры менеджеров")
    void shouldWorkWithUtilityClass() {

        assertNotNull(task, "Задача через утилитарный класс не создана");
    }

    @Test
    @DisplayName("Задачи с заданным id и сгенерированным id не должны конфликтовать внутри менеджера")
    void shouldWorkWithDifferentIds() {
        Task task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание 2"));

        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    @DisplayName("Проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер")
    void shouldInvarianceAllFieldWhenAddTaskInManager() {
        Task taskFromManager = taskManager.getTask(task.getId());
        assertEquals("Задача", taskFromManager.getName(), "Наименование задачи изменено.");
        assertEquals(TaskStatus.NEW, taskFromManager.getStatus(), "Статус задачи изменен.");
        assertEquals("Описание", taskFromManager.getDescription(), "Описание задачи изменено.");
    }
}