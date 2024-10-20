package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.*;

import java.io.*;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        saveToFile();
        return newTask;
    }

    @Override
    public Task createTask(Task task, int id) {
        Task newTask = super.createTask(task, id);
        saveToFile();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        saveToFile();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        saveToFile();
        return newSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveToFile();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        saveToFile();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        saveToFile();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        saveToFile();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        saveToFile();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        saveToFile();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        saveToFile();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        saveToFile();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        saveToFile();
    }

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }

    private Task fromString(String line) {
        final String[] columns = line.split(",");
        String id = columns[0];
        String name = columns[2];
        String description = columns[3];
        String status = columns[4];
        String epicId = columns[5];
        TaskType type = TaskType.valueOf(columns[1]);
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(Integer.parseInt(id), name, TaskStatus.valueOf(status), description);
                break;
            case SUBTASK:
                task = new Subtask(Integer.parseInt(id), name, TaskStatus.valueOf(status), description, Integer.parseInt(epicId));
                break;
            case EPIC:
                task = new Epic(Integer.parseInt(id), name, TaskStatus.valueOf(status), description);
                break;
        }
        return task;
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(HEADER);
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Subtask> entry : subTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new FileException("Ошибка записи в файл: " + file.getAbsolutePath(), exception);
        }
    }

    private void loadFromFile() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line);
                final int id = task.getId();
                if (task.getType() == TaskType.TASK) {
                    tasks.put(id, task);
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(id, (Epic) task);
                } else {
                    subTasks.put(id, (Subtask) task);
                    epics.get(((Subtask) task).getEpicId()).addSubTask(id);
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
        } catch (IOException exception) {
            throw new FileException("Ошибка записи в файл: " + file.getAbsolutePath(), exception);
        }
        seq = maxId;
    }
}
