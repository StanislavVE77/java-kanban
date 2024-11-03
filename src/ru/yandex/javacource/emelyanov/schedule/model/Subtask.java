package ru.yandex.javacource.emelyanov.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, TaskStatus status, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, TaskStatus status, String description, int epicId) {
        super(name, status, description, Duration.ofMinutes(15), LocalDateTime.now());
        this.epicId = epicId;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description, Duration.ofMinutes(15), LocalDateTime.now());
        this.epicId = epicId;
    }

    public boolean setEpicId(int epicId) {
        if (this.getId() == epicId) {
            return false;
        } else {
            this.epicId = epicId;
            return true;
        }
    }

    public Integer getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Subtask{" +
                "subtaskId=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                ", epicId=" + this.epicId +
                '}';
    }
}

