package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Task createTask(Task task, int id);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSubtask(int subtaskId);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    void deleteAllTask();

    void deleteAllEpics();

    void deleteAllSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int epicId);
}
