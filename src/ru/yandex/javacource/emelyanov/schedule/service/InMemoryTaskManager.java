package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subTasks;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));

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
        if (checkTaskCrossingTime(task)) {
            throw new TaskValidationException("Задача пересекаются по времени");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task, int id) {
        if (checkTaskCrossingTime(task)) {
            throw new TaskValidationException("Задача пересекаются по времени");
        }
        if (checkNotExistId(id)) {
            task.setId(id);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
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
        if (checkSubtaskCrossingTime(subtask)) {
            throw new TaskValidationException("Подзадача пересекается по времени");
        }
        Epic epic = epics.get(subtask.getEpicId());
        Optional.of(epic).orElseThrow(() -> new NotFoundException("Задача с id=" + subtask.getEpicId() + " не найдена."));
        subtask.setId(generateId());
        subTasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        epic.addSubTask(subtask);
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        return subtask;
    }

    @Override
    public Task getTask(int taskId) {
        final Task task = tasks.get(taskId);
        Optional.of(task).orElseThrow(() -> new NotFoundException("Задача с id=" + taskId + " не найдена."));
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
        Optional.of(subtask).orElseThrow(() -> new NotFoundException("Задача с id=" + subtaskId + " не найдена."));
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
        if (checkTaskCrossingTimeForUpdate(task)) {
            throw new TaskValidationException("Задача пересекаются по времени");
        }
        Task currentTask = Optional.ofNullable(getTask(task.getId())).orElseThrow(() -> new NotFoundException("Task id = " + task.getId()));
        prioritizedTasks.remove(currentTask);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
        if (checkSubtaskCrossingTimeForUpdate(subtask)) {
            throw new TaskValidationException("Подзадача пересекается по времени");
        }
        Subtask currentSubtask = Optional.ofNullable(getSubtask(subtask.getId())).orElseThrow(() -> new NotFoundException("Subtask id = " + subtask.getId()));
        prioritizedTasks.remove(currentSubtask);
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
        prioritizedTasks.add(subtask);
        updateEpicStatus(savedEpic);
        updateEpicDuration(savedEpic);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic savedEpic = epics.get(epicId);
        savedEpic.getSubTasks().stream().forEach(subTasks::remove);
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
        updateEpicDuration(epic);
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
            updateEpicDuration(epic);
        }
        subTasks.clear();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = new ArrayList<>();
        epic.getSubTasks().stream().map(subTasks::get).forEach(epicSubtasks::add);
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getAll();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int generateId() {
        int newId = ++seq;
        while (!checkNotExistId(newId)) {
            newId = ++seq;
        }
        return newId;
    }

    public void updateEpicStatus(Epic epic) {
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

    public void updateEpicDuration(Epic epic) {
        List<Subtask> epicSubTasks = getEpicSubtasks(epic.getId());
        Duration duration = Duration.ofMinutes(0);
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        for (Subtask st : epicSubTasks) {
            if (st == null) {
                continue;
            }
            if (st.getStartTime().isBefore(start)) {
                start = st.getStartTime();
            }
            if (st.getEndTime().isAfter(end)) {
                end = st.getEndTime();
            }
            duration = duration.plus(st.getDuration());
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    public boolean checkNotExistId(int id) {
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

    private Boolean checkTaskCrossingTimeForUpdate(Task task) {
        if (tasks.isEmpty()) {
            return false;
        }
        Optional<Map.Entry<Integer, Task>> savedTask = tasks.entrySet()
                .stream()
                .filter(t -> (task.getStartTime().isAfter(t.getValue().getStartTime()) && task.getStartTime().isBefore(t.getValue().getEndTime()))
                        || (t.getValue().getStartTime().isAfter(task.getStartTime()) && t.getValue().getStartTime().isBefore(task.getEndTime())) || task.getStartTime().equals(t.getValue().getStartTime()))
                .filter(t -> t.getValue().getId() != task.getId())
                .findFirst();
        if (savedTask.isPresent()) {
            return true;
        }
        return false;
    }

    private Boolean checkTaskCrossingTime(Task task) {
        if (tasks.isEmpty()) {
            return false;
        }
        Optional<Map.Entry<Integer, Task>> savedTask = tasks.entrySet()
                .stream()
                .filter(t -> (task.getStartTime().isAfter(t.getValue().getStartTime()) && task.getStartTime().isBefore(t.getValue().getEndTime()))
                        || (t.getValue().getStartTime().isAfter(task.getStartTime()) && t.getValue().getStartTime().isBefore(task.getEndTime())) || task.getStartTime().equals(t.getValue().getStartTime()))
                .findFirst();
        if (savedTask.isPresent()) {
            return true;
        }
        return false;
    }

    private Boolean checkSubtaskCrossingTime(Subtask subtask) {
        if (subTasks.isEmpty()) {
            return false;
        }
        Optional<Map.Entry<Integer, Subtask>> savedSubtask = subTasks.entrySet()
                .stream()
                .filter(t -> (subtask.getStartTime().isAfter(t.getValue().getStartTime()) && subtask.getStartTime().isBefore(t.getValue().getEndTime()))
                        || (t.getValue().getStartTime().isAfter(subtask.getStartTime()) && t.getValue().getStartTime().isBefore(subtask.getEndTime())) || subtask.getStartTime().equals(t.getValue().getStartTime()))
                .findFirst();
        if (savedSubtask.isPresent()) {
            return true;
        }
        return false;
    }

    private Boolean checkSubtaskCrossingTimeForUpdate(Subtask subtask) {
        if (subTasks.isEmpty()) {
            return false;
        }
        Optional<Map.Entry<Integer, Subtask>> savedSubtask = subTasks.entrySet()
                .stream()
                .filter(t -> (subtask.getStartTime().isAfter(t.getValue().getStartTime()) && subtask.getStartTime().isBefore(t.getValue().getEndTime()))
                        || (t.getValue().getStartTime().isAfter(subtask.getStartTime()) && t.getValue().getStartTime().isBefore(subtask.getEndTime())) || subtask.getStartTime().equals(t.getValue().getStartTime()))
                .filter(t -> t.getValue().getId() != subtask.getId())
                .findFirst();
        if (savedSubtask.isPresent()) {
            return true;
        }
        return false;
    }

}


