package ru.yandex.javacource.emelyanov.schedule.service;

public final class Managers {

    static HistoryManager getDefaultHistory(){
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }

    static TaskManager getDefault() {
        TaskManager taskManager = new InMemoryTaskManager();
        return taskManager;
    }
}
