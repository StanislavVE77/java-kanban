package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subTasks;

    //InMemoryHistoryManager historyManager;
    private final HistoryManager historyManager;
    protected int seq = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task createTask(Task task, int id) {
        if (checkNotExistId(id)) {
            task.setId(id);
            tasks.put(task.getId(), task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.removeAllSubTasks();
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        subtask.setId(generateId());
        subTasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task getTask(int taskId) {
        final Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int epicId) {
        final Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        final Subtask subtask = subTasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.subTasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epic.setSubTasks(savedEpic.getSubTasks());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
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

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic savedEpic = epics.get(epicId);
        for (int stId : savedEpic.getSubTasks()) {
            subTasks.remove(stId);
        }
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subTasks.remove(subtaskId);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubTask(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int stId : epic.getSubTasks()) {
            epicSubtasks.add(subTasks.get(stId));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getAll();
    }

    private int generateId() {
        int newId = ++seq;
        while (!checkNotExistId(newId)) {
            newId = ++seq;
        }
        return newId;
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> epicSubTasks = getEpicSubtasks(epic.getId());
        TaskStatus status = TaskStatus.DONE;
        for (Subtask st : epicSubTasks) {
            if (st == null) {
                continue;
            }
            if (st.getStatus() == TaskStatus.IN_PROGRESS && status != TaskStatus.NEW) {
                status = TaskStatus.IN_PROGRESS;
            }
            if (st.getStatus() == TaskStatus.NEW) {
                status = TaskStatus.NEW;
            }
        }
        epic.setStatus(status);
    }

    private boolean checkNotExistId(int id) {
        List<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            if (task.getId() == id) {
                return false;
            }
        }
        List<Epic> epics = getAllEpics();
        for (Task epic : epics) {
            if (epic.getId() == id) {
                return false;
            }
        }
        List<Subtask> subtasks = getAllSubtasks();
        for (Task subtask : subtasks) {
            if (subtask.getId() == id) {
                return false;
            }
        }
        return true;
    }
}


