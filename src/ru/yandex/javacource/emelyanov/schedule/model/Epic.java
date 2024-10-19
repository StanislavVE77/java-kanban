package ru.yandex.javacource.emelyanov.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public void addSubTask(Subtask subtask) {
        if (this.getId() == subtask.getId()) {
            return;
        } else {
            subtaskIds.add(subtask.getId());
        }
    }

    public boolean addSubTask(Integer id) {
        if (this.getId() == id) {
            return false;
        } else {
            subtaskIds.add(id);
            return true;
        }
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

    public Integer getEpicId() {
        return null;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicId=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
