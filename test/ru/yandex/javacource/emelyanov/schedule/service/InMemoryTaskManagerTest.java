package ru.yandex.javacource.emelyanov.schedule.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void addNewTaskAndDelete() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Test addNewTask", TaskStatus.NEW,"Test addNewTask description");
        //final int taskId = taskManager.createTask(task);

        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void checkTwoTasksWithEqualIds() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Test addNewTask", TaskStatus.NEW,"Test addNewTask description");

        final int taskId = taskManager.createTask(task).getId();

        final Task oneTask = taskManager.getTask(taskId);
        final Task twoTask = taskManager.getTask(taskId);

        assertEquals(oneTask, twoTask, "Задачи не совпадают.");
    }


    @Test
    //проверьте, что наследники класса Task равны друг другу, если равен их id
    void checkTwoEpicsAndTwoSubtasksWithEqualIds() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test addNewEpic", TaskStatus.NEW,"Test addNewEpic description");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask = new Subtask("Test addNewSubtask", TaskStatus.NEW, "Test addNewSubtask description", epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        final Epic oneEpic = taskManager.getEpic(epicId);
        final Epic twoEpic = taskManager.getEpic(epicId);
        assertEquals(oneEpic, twoEpic, "Эпики не совпадают.");

        final Subtask oneSubtask = taskManager.getSubtask(subtaskId);
        final Subtask twoSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(oneSubtask, twoSubtask, "Сабтаски не совпадают.");

    }

    @Test
    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи
    void checkEpicCannotBeAddedToItselfAsASubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test addNewEpic", TaskStatus.NEW,"Test addNewEpic description");
        final int epicId = taskManager.createEpic((Epic) epic).getId();

        boolean isSubtaskAdded = epic.addSubTask(epicId);
        assertFalse(isSubtaskAdded, "Эпик добавлен в самого себя как сабтаск");
    }

    @Test
        // проверьте, что объект Subtask нельзя сделать своим же эпиком
    void checkSubtaskCannotBeEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test addNewEpic", TaskStatus.NEW,"Test addNewEpic description");
        final int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask = new Subtask("Test addNewSubtask", TaskStatus.NEW, "Test addNewSubtask description", epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        boolean isSubtaskChanged = subtask.setEpicId(subtaskId);
        assertFalse(isSubtaskChanged, "Subtask стал своим же эпиком");
    }

    @Test
    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    void checkWorkWithInMemoryTaskManagerClass() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = taskManager.createTask(new Task("Test addNewTask", TaskStatus.NEW, "Test addNewTask description"));
        Epic epic = taskManager.createEpic(new Epic("Test addNewEpic", TaskStatus.NEW, "Test addNewEpic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Test addNewSubtask", TaskStatus.DONE, "Test addNewSubtask description", epic.getId()));

        Task taskFromManager = taskManager.getTask(task.getId());
        assertEquals(task.getName(), taskFromManager.getName(), "Не найдена задача по id");

        Epic epicFromManager = taskManager.getEpic(epic.getId());
        assertEquals(epic.getName(), epicFromManager.getName(), "Не найден эпик по id");

        Subtask subtaskFromManager = taskManager.getSubtask(subtask.getId());
        assertEquals(subtask.getName(), subtaskFromManager.getName(), "Не найдена подзадача по id");
    }
}