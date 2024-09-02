package ru.yandex.javacource.Emelyamov.schedule.service;

import ru.yandex.javacource.Emelyamov.schedule.model.Task;
import ru.yandex.javacource.Emelyamov.schedule.model.Epic;
import ru.yandex.javacource.Emelyamov.schedule.model.Subtask;
import ru.yandex.javacource.Emelyamov.schedule.model.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subTasks;
    int seq = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public Task createTask(Task task) {
        task.setTaskId(generateId());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setTaskId(generateId());
        epic.setStatus(TaskStatus.NEW);
        epic.removeAllSubTasks();
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        subtask.setTaskId(generateId());
        subTasks.put(subtask.getTaskId(), subtask);
        epic.addSubTask(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtask(int subtaskId) {
        return subTasks.get(subtaskId);
    }

/*
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }
*/
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(this.epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.subTasks.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getTaskId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription((epic.getDescription()));
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getTaskId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subTasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) {
            return;
        }
        subTasks.put(id, subtask);
        updateEpicStatus(savedEpic);
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        Epic savedEpic = epics.get(epicId);
        for (int stId : savedEpic.getSubTasks()) {
            subTasks.remove(stId);
        }
        epics.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subTasks.remove(subtaskId);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubTask(subtask);
        updateEpicStatus(epic);
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int stId : epic.getSubTasks()) {
            epicSubtasks.add(subTasks.get(stId));
        }
        return epicSubtasks;
    }

    private int generateId() {
        return ++seq;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> epicSubTasks = getEpicSubtasks(epic.getTaskId());
        TaskStatus status = TaskStatus.DONE;
        for (Subtask st : epicSubTasks) {
            if (st.getStatus() == TaskStatus.IN_PROGRESS && status != TaskStatus.NEW) {
                status = TaskStatus.IN_PROGRESS;
            }
            if (st.getStatus() == TaskStatus.NEW) {
                status = TaskStatus.NEW;
            }
        }
        //TaskStatus status = TaskStatus.NEW;
        epic.setStatus(status);
    }

}
