package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task1 = taskManager.createTask(new Task("Задача 1", TaskStatus.NEW,"Описание 1"));
        task1.setId(1);
        task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW,"Описание 2"));
        task2.setId(2);
        task3 = taskManager.createTask(new Task("Задача 3", TaskStatus.NEW,"Описание 3"));
        task3.setId(3);
    }

    @Test
    @DisplayName("Проверка добавления 1 задачи в историю")
    void shouldAddOneTaskToHistory() {
        historyManager.add(task1);

        final List<Task> history = historyManager.getAll();

        assertNotNull(history, "Список задач в истории пустой.");
        assertEquals(1, history.size(), "Количество задач в списке "+ history.size() + " не равно 1.");
        assertEquals(history.getFirst(), history.getLast(), "Первый элемент и последний элемент истории не совпадают.");
    }

    @Test
    @DisplayName("Проверка добавления 2 задач в историю")
    void shouldAddTwoTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        final List<Task> history = historyManager.getAll();

        assertNotNull(history, "Список задач в истории пустой.");
        assertEquals(2, history.size(), "Количество задач в списке "+ history.size() + " не равно 2.");
        assertNotEquals(history.getFirst(), history.getLast(), "Первый элемент и последний элемент истории совпадают.");
    }

    @Test
    @DisplayName("Проверка добавления 11 разных задач в историю")
    void shouldAddElevenDiferentTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        for (int i = 4; i <= 11; i++) {
            Task task = taskManager.createTask(new Task("Задача " + i, TaskStatus.NEW,"Описание " + i));
            task.setId(i);
            historyManager.add(task);
        }

        final List<Task> history = historyManager.getAll();

        assertNotNull(history, "Список задач в истории пустой.");
        assertEquals(11, history.size(), "Количество задач в списке "+ history.size() + " не равно 11.");
        assertEquals(history.getFirst().getId(), 1, "Первый элемент поменялся.");
        assertEquals(history.getLast().getId(), 11, "Последния добавленная задача не стала последним элементов в истории.");
    }

    @Test
    @DisplayName("Удаление начального элемента истории")
    void shouldFirstTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getAll();

        assertEquals(2, history.size(), "Количество задач в списке "+ history.size() + " не равно 2.");
        assertEquals(history.getLast().getId(), 3, "Изменился последний элемент истории.");
        assertEquals(history.getFirst().getId(), 2, "Первый элемент истории не изменился.");
    }

    @Test
    @DisplayName("Удаление элемента истории из середины списка")
    void shouldMiddleTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertEquals(historyManager.getAll(), List.of(task1, task3));
    }

    @Test
    @DisplayName("Удаление последнего элемента истории")
    void shouldEndTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        assertEquals(historyManager.getAll(), List.of(task1, task2));
    }

    @Test
    @DisplayName("При добавлении в историю существующей задачи из начала истории, новая задача добавляется в конец,старая задача удаляется")
    void shouldFirstDuplicatedTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        final List<Task> history = historyManager.getAll();

        assertEquals(3, history.size(), "Количество задач в списке "+ history.size() + " не равно 3.");
        assertEquals(history.getLast().getId(), 1, "Задача task1 в конец истории не добавилась.");
        assertEquals(history.getFirst().getId(), 2, "Первый элемент истории не поменялся.");
    }

    @Test
    @DisplayName("При добавлении в историю существующей задачи из середины истории, новая задача добавляется в конец,старая задача удаляется")
    void shouldMiddleDuplicatedTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);

        final List<Task> history = historyManager.getAll();

        assertEquals(3, history.size(), "Количество задач в списке "+ history.size() + " не равно 3.");
        assertEquals(history.getLast().getId(), 2, "Задача task2 в конец истории не добавилась.");
        assertEquals(history.getFirst().getId(), 1, "Первый элемент истории изменился.");
    }

    @Test
    @DisplayName("При добавлении в историю существующей задачи из конца истории, история не меняется")
    void shouldEndDuplicatedTaskDeleted() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task3);

        final List<Task> history = historyManager.getAll();

        assertEquals(3, history.size(), "Количество задач в списке "+ history.size() + " не равно 3.");
        assertEquals(history.getLast().getId(), 3, "Последний элемент истории изменился.");
        assertEquals(history.getFirst().getId(), 1, "Первый элемент истории изменился.");
    }

}