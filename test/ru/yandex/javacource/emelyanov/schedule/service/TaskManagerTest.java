package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    Task task;

    protected abstract T createManager();

    @BeforeEach
    void beforeEach() {
        taskManager = createManager();
        task = taskManager.createTask(new Task("Название задачи", TaskStatus.NEW, "Описание задачи"));
    }

    @Test
    @DisplayName("Добавление новой задачи")
    void shouldCreateTask() {
        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Проверка, что экземпляры класса Task равны друг другу, если равен их id")
    void checkTwoTasksWithEqualIds() {
        final Task oneTask = taskManager.getTask(task.getId());
        final Task twoTask = taskManager.getTask(task.getId());

        assertEquals(oneTask, twoTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Проверка, что наследники класса Task равны друг другу, если равен их id")
    void checkTwoEpicsAndTwoSubtasksWithEqualIds() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask = new Subtask("Название сабтаска", TaskStatus.NEW, "Описание сабтаска", epicId, Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T19:00:00"));
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        final Epic oneEpic = taskManager.getEpic(epicId);
        final Epic twoEpic = taskManager.getEpic(epicId);
        assertEquals(oneEpic, twoEpic, "Эпики не совпадают.");

        final Subtask oneSubtask = taskManager.getSubtask(subtaskId);
        final Subtask twoSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(oneSubtask, twoSubtask, "Сабтаски не совпадают.");
    }

    @Test
    @DisplayName("Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи")
    void checkEpicCannotBeAddedToItselfAsASubtask() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic((Epic) epic).getId();

        boolean isSubtaskAdded = epic.addSubTask(epicId);
        assertFalse(isSubtaskAdded, "Эпик добавлен в самого себя как сабтаск");
    }

    @Test
    @DisplayName("Проверьте, что объект Subtask нельзя сделать своим же эпиком")
    void checkSubtaskCannotBeEpic() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask = new Subtask("Название сабтаска", TaskStatus.NEW, "Описание сабтаска", epicId, Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T15:00:00"));
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        boolean isSubtaskChanged = subtask.setEpicId(subtaskId);
        assertFalse(isSubtaskChanged, "Сабтаск стал своим же эпиком");
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id")
    void checkWorkWithInMemoryTaskManagerClass() {
        Epic epic = taskManager.createEpic(new Epic("Название эпика", TaskStatus.NEW, "Описание эпика"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Название сабтаска", TaskStatus.DONE, "Описание сабтаска", epic.getId(), Duration.ofMinutes(1), LocalDateTime.parse("2020-01-11T18:00:00")));

        Task taskFromManager = taskManager.getTask(task.getId());
        assertEquals(task.getName(), taskFromManager.getName(), "Не найдена задача по id");

        Epic epicFromManager = taskManager.getEpic(epic.getId());
        assertEquals(epic.getName(), epicFromManager.getName(), "Не найден эпик по id");

        Subtask subtaskFromManager = taskManager.getSubtask(subtask.getId());
        assertEquals(subtask.getName(), subtaskFromManager.getName(), "Не найдена подзадача по id");
    }

    @Test
    @DisplayName("Проверка полей duration, startTime, endTime у задачи")
    void shouldCreateTaskWithDateTimeFields() {
        Task taskOther = taskManager.createTask(new Task("Название задачи 2", TaskStatus.NEW, "Описание задачи 2", Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));

        assertTrue(task.getDuration().equals(Duration.ofMinutes(15)), "Задача создалась без значения по-умолчанию у duration");
        assertNotNull(task.getStartTime(), "Для созданной задачи не указан startTime по-умолчанию");
        assertTrue(task.getEndTime().isAfter(task.getStartTime()), "Для созданной задачи не вычисляется endTime по-умолчанию");

        assertTrue(taskOther.getEndTime().equals(LocalDateTime.parse("2000-10-30T12:40:00.000000000")), "Не вычисляется endTime у задачи, созданной с указанием duration и startTime");
    }

    @Test
    @DisplayName("Проверка вычисления полей duration, startTime, endTime у эпика")
    void shouldCalculateDateTimeEpicFields() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название сабтаска 1", TaskStatus.NEW, "Описание сабтаска 1", epicId, Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название сабтаска 2", TaskStatus.NEW, "Описание сабтаска 2", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 14, 40, 0)));

        assertTrue(epic.getDuration().equals(Duration.ofMinutes(30)), "Не правильно вычисляется duration у эпика");
        assertTrue(epic.getStartTime().equals(LocalDateTime.parse("2000-10-30T12:30:00.000000000")), "Не правильно вычисляется startTime у эпика");
        assertTrue(epic.getEndTime().equals(LocalDateTime.parse("2000-10-31T15:00:00.000000000")), "Не правильно вычисляется endTime у эпика");
    }

    @Test
    @DisplayName("Проверка вычисления статуса у эпика: Все подзадачи со статусом NEW")
    void shouldCalculateEpicStatusNew() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название сабтаска 1", TaskStatus.NEW, "Описание сабтаска 1", epicId, Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название сабтаска 2", TaskStatus.NEW, "Описание сабтаска 2", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 14, 40, 0)));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название сабтаска 3", TaskStatus.NEW, "Описание сабтаска 3", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Не правильно вычисляется статус у эпика.");
    }

    @Test
    @DisplayName("Проверка вычисления статуса у эпика: Все подзадачи со статусом DONE")
    void shouldCalculateEpicStatusDone() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название сабтаска 1", TaskStatus.DONE, "Описание сабтаска 1", epicId, Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название сабтаска 2", TaskStatus.DONE, "Описание сабтаска 2", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 14, 40, 0)));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название сабтаска 3", TaskStatus.DONE, "Описание сабтаска 3", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));

        assertEquals(epic.getStatus(), TaskStatus.DONE, "Не правильно вычисляется статус у эпика.");
    }

    @Test
    @DisplayName("Проверка вычисления статуса у эпика: Все подзадачи со статусом NEW и DONE")
    void shouldCalculateEpicStatusNewDone() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название сабтаска 1", TaskStatus.DONE, "Описание сабтаска 1", epicId, Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название сабтаска 2", TaskStatus.NEW, "Описание сабтаска 2", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 14, 40, 0)));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название сабтаска 3", TaskStatus.DONE, "Описание сабтаска 3", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Не правильно вычисляется статус у эпика.");
    }

    @Test
    @DisplayName("Проверка вычисления статуса у эпика: Все подзадачи со статусом IN_PROGRESS")
    void shouldCalculateEpicStatusInProgress() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название сабтаска 1", TaskStatus.IN_PROGRESS, "Описание сабтаска 1", epicId, Duration.ofMinutes(10), LocalDateTime.of(2000, 10, 30, 12, 30, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название сабтаска 2", TaskStatus.IN_PROGRESS, "Описание сабтаска 2", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 14, 40, 0)));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название сабтаска 3", TaskStatus.IN_PROGRESS, "Описание сабтаска 3", epicId, Duration.ofMinutes(20), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Не правильно вычисляется статус у эпика.");
    }

    @Test
    @DisplayName("Задачи не должны пересекаться по времени")
    void shouldNotCrossTask() {
        Task task1 = taskManager.createTask(new Task("Название задачи 1", TaskStatus.NEW, "Описание задачи", Duration.ofMinutes(15), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));
        Task task2 = taskManager.createTask(new Task("Название задачи 2", TaskStatus.NEW, "Описание задачи", Duration.ofMinutes(15), LocalDateTime.of(2000, 10, 31, 16, 40, 0)));

        assertEquals(taskManager.getAllTasks().size(), 3, "Пересечений нет. Должно быть 3 задачи");
    }

    @Test
    @DisplayName("Задачи пересекаются по времени")
    void shouldCrossTask() {
        Task task1 = taskManager.createTask(new Task("Название задачи 1", TaskStatus.NEW, "Описание задачи", Duration.ofMinutes(15), LocalDateTime.of(2000, 10, 31, 15, 40, 0)));
        Task task2 = taskManager.createTask(new Task("Название задачи 2", TaskStatus.NEW, "Описание задачи", Duration.ofMinutes(15), LocalDateTime.of(2000, 10, 31, 15, 45, 0)));

        assertEquals(taskManager.getAllTasks().size(), 2, "Есть пересечение 2-х задач.");
    }
}
