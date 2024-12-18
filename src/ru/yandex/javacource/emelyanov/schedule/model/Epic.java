package ru.yandex.javacource.emelyanov.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description, Duration.ofMinutes(0), LocalDateTime.now());
        this.endTime = LocalDateTime.now();
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description, Duration.ofMinutes(0), LocalDateTime.now());
        this.endTime = LocalDateTime.now();
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Epic{" +
                "epicId=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(formatter) +
                ", endTime=" + endTime.format(formatter) +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
