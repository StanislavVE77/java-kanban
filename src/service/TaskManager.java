package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subTasks;
    int seq = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++seq;
    }

    public Task createTask(Task task) {
        task.setTaskId(generateId());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        Epic epicNew = new Epic(epic.getName(), TaskStatus.NEW, epic.getDescription());
        epicNew.setTaskId(generateId());
        epics.put(epicNew.getTaskId(), epicNew);
        return epicNew;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setTaskId(generateId());
        subTasks.put(subtask.getTaskId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addTask(subtask);
        calculateEpicStatus(epic);
        return subtask;
    }

    public void calculateEpicStatus(Epic epic) {
        TaskStatus status = TaskStatus.NEW;
        epic.setStatus(status);
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
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subTasks;
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getTaskId());
        if (savedEpic == null) { return; }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription((epic.getDescription()));
    }

    public void updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) { return; }
        calculateEpicStatus(savedEpic);
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
        subTasks.remove(subtaskId);
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubtasks() {
        subTasks.clear();
    }

}
