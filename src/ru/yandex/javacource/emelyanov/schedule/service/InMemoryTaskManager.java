package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subTasks;
    //InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    int seq = 0;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    @Override
    public Task createTask(Task task) {
        //int newId = generateId();
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
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        historyManager.add(subTasks.get(subtaskId));
        return subTasks.get(subtaskId);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.subTasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription((epic.getDescription()));
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
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int stId : epic.getSubTasks()) {
            epicSubtasks.add(subTasks.get(stId));
        }
        return epicSubtasks;
    }

    private int generateId() {
        int newId = ++seq;
        while (!checkNotExistId(newId)) {
            newId = ++seq;
        }
        return newId;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> epicSubTasks = getEpicSubtasks(epic.getId());
        TaskStatus status = TaskStatus.DONE;
        for (Subtask st : epicSubTasks) {
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
        ArrayList<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            if (task.getId() == id) {
                return false;
            }
        }
        ArrayList<Epic> epics = getAllEpics();
        for (Task epic : epics) {
            if (epic.getId() == id) {
                return false;
            }
        }
        ArrayList<Subtask> subtasks = getAllSubtasks();
        for (Task subtask : subtasks) {
            if (subtask.getId() == id) {
                return false;
            }
        }
        return true;
    }

    public List<Task> getHistory(){
        return historyManager.historyList;
    }
}


