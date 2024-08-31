package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
    }

    public void addTask(Subtask subTask) {
        subTasks.add(subTask.getTaskId());
    }

    public void removeTask(Subtask subTask) {
        subTasks.remove(subTask.getTaskId());
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
    }
}
