package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static final int SIZE_OF_HISTORY = 10;
    private List<Task> history = new ArrayList<>(SIZE_OF_HISTORY);

    public InMemoryHistoryManager() {
        this.history = history;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        //Task newTask = new Task(task.getName(), task.getStatus(),task.getDescription());
        if (task == null) {
            return;
        }
        if (history.size() >= SIZE_OF_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }
}
