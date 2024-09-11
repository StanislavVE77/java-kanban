package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
        // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    void checkWorkWithUtilityClass() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test addNewTask", TaskStatus.NEW,"Test addNewTask description");

        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача через утилитарный класс не создана");
    }
    @Test
        // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    void  checkWorkWithDifferentIds() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = taskManager.createTask(new Task("Test addNewTask", TaskStatus.NEW, "Test addNewTask description"), 1);
        Task task2 = taskManager.createTask(new Task("Test addNewTask", TaskStatus.NEW, "Test addNewTask description"));

        final ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
        // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    void checkInvarianceAllFieldWhenAddTaskInManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("Test addNewTask", TaskStatus.NEW, "Test addNewTask description"), 1);
        Task taskFromManager = taskManager.getTask(task.getId());
        assertEquals("Test addNewTask", taskFromManager.getName(), "Наименование задачи изменено.");
        assertEquals(TaskStatus.NEW, taskFromManager.getStatus(), "Статус задачи изменен.");
        assertEquals("Test addNewTask description", taskFromManager.getDescription(), "Описание задачи изменено.");
    }

}