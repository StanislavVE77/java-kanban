package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> historyList = new ArrayList<>(SIZE_OF_HISTORY);

    public InMemoryHistoryManager() {
        this.historyList = historyList;
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public boolean add(Task task) {
        Task newTask = new Task(task.getName(), task.getStatus(),task.getDescription());
        if (historyList.size() == SIZE_OF_HISTORY) {
            for (int i = 0; i < SIZE_OF_HISTORY - 1; i++) {
                historyList.set(i, historyList.get(i + 1));
            }
            historyList.remove(SIZE_OF_HISTORY - 1);
        }
        return historyList.add(newTask);
    }
}
