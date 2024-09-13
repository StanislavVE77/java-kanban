package ru.yandex.javacource.emelyanov.schedule;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;
import ru.yandex.javacource.emelyanov.schedule.service.InMemoryHistoryManager;
import ru.yandex.javacource.emelyanov.schedule.service.InMemoryTaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.TaskManager;
import ru.yandex.javacource.emelyanov.schedule.service.Managers;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task = taskManager.createTask(new Task("New task", TaskStatus.NEW, "Description task"));
        System.out.println("Create Task: " + task);

        Task taskFromManager = taskManager.getTask(task.getId());
        System.out.println("Get Task: " + taskFromManager);

        Task taskUpdated = new Task(taskFromManager.getId(), "New task Update", TaskStatus.DONE, "Description Update");
        taskManager.updateTask(taskUpdated);
        System.out.println("Update Task: " + taskUpdated);

        taskManager.deleteTask(taskFromManager.getId());
        System.out.println("Delete Task: " + task);

        Epic epic = taskManager.createEpic(new Epic("New epic", "Description epic"));
        System.out.println("Create Epic: " + epic);

        Subtask subtask = taskManager.createSubtask((new Subtask("New subtask", TaskStatus.NEW, "Description subtask", epic.getId())));
        System.out.println("Create Subtask: " + subtask);

        Subtask subtask_2 = taskManager.createSubtask((new Subtask("New subtask 2", TaskStatus.IN_PROGRESS, "Description subtask 2", epic.getId())));
        System.out.println("Create Subtask 2: " + subtask_2);

        Epic epicFromManager = taskManager.getEpic(epic.getId());
        System.out.println("Get Epic: " + epicFromManager);

        Subtask subtaskFromManager = taskManager.getSubtask(subtask.getId());
        System.out.println("Get Subtask: " + subtaskFromManager);

        Subtask subtaskFromManager2 = taskManager.getSubtask(subtask_2.getId());
        System.out.println("Get Subtask 2: " + subtaskFromManager2);

        Epic epicUpdated = new Epic(epicFromManager.getId(), "New epic Update", TaskStatus.DONE, "Description Epic Update");
        taskManager.updateEpic(epicUpdated);
        System.out.println("Update Epic: " + epicUpdated);

        Subtask subtaskUpdated = new Subtask(subtaskFromManager.getId(), "New subtask Update", TaskStatus.DONE, "Description Subtask Update", epic.getId());
        taskManager.updateSubtask(subtaskUpdated);
        System.out.println("Update Subtask: " + subtaskUpdated);

        taskManager.deleteSubtask(subtaskFromManager.getId());
        System.out.println("Delete Subtask: " + subtask);


        taskManager.deleteEpic(epicFromManager.getId());
        System.out.println("Delete Epic: " + epic);


        System.out.println("-------- Созданние объектов для тестирования History -------------------------------------");
        Task task1 = taskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание задачи 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание задачи 2"));
        Task task3 = taskManager.createTask(new Task("Задача 3", TaskStatus.NEW, "Описание задачи 3"));
        Task task4 = taskManager.createTask(new Task("Задача 4", TaskStatus.NEW, "Описание задачи 4"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", TaskStatus.NEW, "Описание эпика 2"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", TaskStatus.NEW, "Описание подзадачи 3", epic2.getId()));
        System.out.println("-------- Добавление объектов в History ---------------------------------------------------");
        taskManager.getTask(task4.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        Task taskForUpdate = new Task(task1.getId(), "Задача 1 UPDATE", TaskStatus.DONE, "Описание задачи 1 UPDATE");
        taskManager.updateTask(taskForUpdate);

        Epic epicForUpdate = new Epic(epic1.getId(), "Эпик 1 UPDATE", TaskStatus.DONE, "Описание эпика 1 UPDATE");
        taskManager.updateEpic(epicForUpdate);

        Subtask subtaskForUpdate = new Subtask(subtask2.getId(), "Подзадача 2 UPDATE", TaskStatus.DONE, "Описание подзадачи 2 UPDATE", epic1.getId());
        taskManager.updateSubtask(subtaskForUpdate);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask2.getId());
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
    }
}
