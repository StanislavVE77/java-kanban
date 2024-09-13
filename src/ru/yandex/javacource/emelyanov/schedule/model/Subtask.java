package ru.yandex.javacource.emelyanov.schedule.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, TaskStatus status, String description, int epicId) {
        super(name, status, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description);
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

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "subtaskId=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + this.epicId +
                '}';
    }
}

