package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.List;

public interface HistoryManager {
    static final int SIZE_OF_HISTORY = 10;

    boolean add(Task task);

    List<Task> getHistory();
}
