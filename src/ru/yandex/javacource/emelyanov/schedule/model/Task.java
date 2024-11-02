package ru.yandex.javacource.emelyanov.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    Duration duration;
    LocalDateTime startTime;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Task(String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }


    public Task(String name, TaskStatus status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = Duration.ofMinutes(15);
        this.startTime = LocalDateTime.now();

    }

    public Task(int id, String name, TaskStatus status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.id = id;
        this.duration = Duration.ofMinutes(15);
        this.startTime = LocalDateTime.now();
    }

    public Task(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ofMinutes(15);
        this.startTime = LocalDateTime.now();
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                '}';
    }
}