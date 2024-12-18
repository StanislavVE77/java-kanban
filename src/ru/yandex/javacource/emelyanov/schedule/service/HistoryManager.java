package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getAll();
}