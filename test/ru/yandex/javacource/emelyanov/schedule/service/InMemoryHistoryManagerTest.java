package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    private Task task;
    InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task = taskManager.createTask(new Task("Задача", TaskStatus.NEW,"Описание"));
    }

    @Test
    @DisplayName("Проверка добавления задачи в историю")
    void shouldAddToHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Список задач в истории пустой.");
        assertEquals(1, history.size(), "Количество задач в списке "+ history.size() + " не равно 1.");
    }

    @Test
    @DisplayName("Задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных")
    void shouldSavingTasksDataInHistoryManager() {
        historyManager.add(task);
        Task taskForUpdate = new Task(task.getId(), "Задача UPDATE", TaskStatus.DONE, "Описание UPDATE");
        taskManager.updateTask(taskForUpdate);
        historyManager.add(taskForUpdate);
        final List<Task> history = historyManager.getHistory();

        assertNotEquals(history.get(0).getName(), history.get(1).getName(), "Предыдущее имя задачи в истории не сохраняется");
        assertNotEquals(history.get(0).getStatus(), history.get(1).getStatus(), "Предыдущий статус задачи в истории не сохраняется");
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription(), "Предыдущее описание задачи в истории не сохраняется");
    }
}