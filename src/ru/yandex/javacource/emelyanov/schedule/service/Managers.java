package ru.yandex.javacource.emelyanov.schedule.service;

public final class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        //return new InMemoryTaskManager(getDefaultHistory());
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
