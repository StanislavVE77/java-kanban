package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addToHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Test addNewTask", TaskStatus.NEW,"Test addNewTask description");

        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
        // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    void checkSavingTasksDataInHistoryManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Test addNewTask", TaskStatus.NEW,"Test addNewTask description");

        final int taskId = taskManager.createTask(task).getId();

        historyManager.add(task);

        task.setName("Test addNewTask 2");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDescription("Test addNewTask 2 description ");
        taskManager.updateTask(task);

        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotEquals(history.get(0).getName(), history.get(1).getName(), "Предыдущее имя задачи в истории не сохраняется");
        assertNotEquals(history.get(0).getStatus(), history.get(1).getStatus(), "Предыдущий статус задачи в истории не сохраняется");
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription(), "Предыдущее описание задачи в истории не сохраняется");
    }

}