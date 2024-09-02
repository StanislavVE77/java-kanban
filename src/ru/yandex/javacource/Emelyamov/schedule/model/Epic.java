package ru.yandex.javacource.Emelyamov.schedule.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
    }

    public void addSubTask(Subtask subtask) {
        subtaskIds.add(subtask.getTaskId());
    }

    public void removeSubTask(Subtask subtask) {
        subtaskIds.remove(subtask);
    }

    public void removeAllSubTasks() {
        subtaskIds.clear();
    }

    public List<Integer> getSubTasks() {
        return subtaskIds;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subtaskIds = subTasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicId=" + getTaskId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
