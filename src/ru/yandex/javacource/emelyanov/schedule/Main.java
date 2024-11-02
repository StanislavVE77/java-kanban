package ru.yandex.javacource.emelyanov.schedule;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;
import ru.yandex.javacource.emelyanov.schedule.service.FileBackedTaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        File file = new File("tasks.csv");
        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        TaskManager taskManager = fileTaskManager.loadFromFile(file);
        System.out.println("-------- Созданние объектов для тестирования History -------------------------------------");
        Task task1 = taskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание задачи 1", Duration.ofMinutes(8), LocalDateTime.of(2023, 8, 13, 11, 54, 0)));
        Task task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание задачи 2", Duration.ofMinutes(20), LocalDateTime.of(2024, 10, 31, 17, 56, 0)));
        Task task3 = taskManager.createTask(new Task("Задача 3", TaskStatus.NEW, "Описание задачи 3"));
        Task task4 = taskManager.createTask(new Task("Задача 4", TaskStatus.NEW, "Описание задачи 4", Duration.ofMinutes(12), LocalDateTime.of(2023, 8, 12, 11, 50, 0)));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId(), Duration.ofMinutes(11), LocalDateTime.of(2024, 10, 31, 17, 56, 0)));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId(), Duration.ofMinutes(11), LocalDateTime.of(2024, 10, 31, 19, 56, 0)));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", TaskStatus.NEW, "Описание эпика 2"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", TaskStatus.NEW, "Описание подзадачи 3", epic2.getId(), Duration.ofMinutes(4), LocalDateTime.of(2024, 5, 1, 11, 11, 0)));
        Subtask subtask4 = taskManager.createSubtask(new Subtask("Подзадача 4", TaskStatus.NEW, "Описание подзадачи 4", epic2.getId(), Duration.ofMinutes(6), LocalDateTime.of(2024, 4, 24, 17, 10, 0)));
        Subtask subtask5 = taskManager.createSubtask(new Subtask("Подзадача 5", TaskStatus.NEW, "Описание подзадачи 5", epic2.getId(), Duration.ofMinutes(10), LocalDateTime.of(2024, 4, 30, 11, 10, 0)));
        System.out.println("-------- Добавление объектов в History ---------------------------------------------------");
        taskManager.getTask(task4.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask2.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());
        taskManager.getTask(task4.getId());
        taskManager.getTask(task4.getId());
        taskManager.getSubtask(subtask2.getId());


        Task taskForUpdate = new Task(task1.getId(), "Задача 1 UPDATE", TaskStatus.DONE, "Описание задачи 1 UPDATE", Duration.ofMinutes(8), LocalDateTime.of(2023, 7, 13, 11, 54, 0));
        taskManager.updateTask(taskForUpdate);

        Epic epicForUpdate = new Epic(epic1.getId(), "Эпик 1 UPDATE", TaskStatus.DONE, "Описание эпика 1 UPDATE");
        taskManager.updateEpic(epicForUpdate);

        Subtask subtaskForUpdate = new Subtask(subtask4.getId(), "Подзадача 4 UPDATE", TaskStatus.DONE, "Описание подзадачи 4 UPDATE", epic1.getId());
        taskManager.updateSubtask(subtaskForUpdate);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask3.getId());
        System.out.println("---  Print All tasks  --------------------------------------------------------------------");

        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("Список приоритетных задач:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }

    }
}