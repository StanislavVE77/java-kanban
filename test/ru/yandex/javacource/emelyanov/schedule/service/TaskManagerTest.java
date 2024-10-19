package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

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

        Subtask subtask = new Subtask("Название сабтаска", TaskStatus.NEW, "Описание сабтаска", epicId);
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

        Subtask subtask = new Subtask("Название сабтаска", TaskStatus.NEW, "Описание сабтаска", epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        boolean isSubtaskChanged = subtask.setEpicId(subtaskId);
        assertFalse(isSubtaskChanged, "Сабтаск стал своим же эпиком");
    }

    @Test
    @DisplayName("Проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id")
    void checkWorkWithInMemoryTaskManagerClass() {
        Epic epic = taskManager.createEpic(new Epic("Название эпика", TaskStatus.NEW, "Описание эпика"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Название сабтаска", TaskStatus.DONE, "Описание сабтаска", epic.getId()));

        Task taskFromManager = taskManager.getTask(task.getId());
        assertEquals(task.getName(), taskFromManager.getName(), "Не найдена задача по id");

        Epic epicFromManager = taskManager.getEpic(epic.getId());
        assertEquals(epic.getName(), epicFromManager.getName(), "Не найден эпик по id");

        Subtask subtaskFromManager = taskManager.getSubtask(subtask.getId());
        assertEquals(subtask.getName(), subtaskFromManager.getName(), "Не найдена подзадача по id");
    }
}
