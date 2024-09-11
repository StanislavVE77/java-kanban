package ru.yandex.javacource.emelyanov.schedule;

import ru.yandex.javacource.emelyanov.schedule.model.Task;
import ru.yandex.javacource.emelyanov.schedule.model.Epic;
import ru.yandex.javacource.emelyanov.schedule.model.Subtask;
import ru.yandex.javacource.emelyanov.schedule.model.TaskStatus;
import ru.yandex.javacource.emelyanov.schedule.service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        System.out.println("-------- Созданные объекты -----------------------------------------------------------------");
        Task task1 = taskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание задачи 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание задачи 2"));
        Task task3 = taskManager.createTask(new Task("Задача 3", TaskStatus.NEW, "Описание задачи 3"));
        Task task4 = taskManager.createTask(new Task("Задача 4", TaskStatus.NEW, "Описание задачи 4"));
        Task task5 = taskManager.createTask(new Task("Задача 5", TaskStatus.NEW, "Описание задачи 5"));
        Task task6 = taskManager.createTask(new Task("Задача 6", TaskStatus.NEW, "Описание задачи 6"));
        Task task7 = taskManager.createTask(new Task("Задача 7", TaskStatus.NEW, "Описание задачи 7"));
        Task task8 = taskManager.createTask(new Task("Задача 8", TaskStatus.NEW, "Описание задачи 8"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", TaskStatus.NEW, "Описание эпика 2"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 4", TaskStatus.NEW, "Описание подзадачи 4", epic2.getId()));
        System.out.println("-------- Формирование истории -----------------------------------------------------------------");
        taskManager.getTask(task4.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setDescription("New task description");
        taskManager.updateTask(task1);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task6.getId());
        taskManager.getTask(task7.getId());

        printAllTasks(taskManager);

        System.out.println("--------------------------------------------------------------------------------------------");

        Task taskFromManager = taskManager.getTask(task1.getId());
        taskFromManager.setStatus(TaskStatus.DONE);
        taskManager.updateTask(taskFromManager);

        Epic epicFromManager = taskManager.getEpic(epic1.getId());
        epicFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epicFromManager);

        Subtask subtaskFromManager = taskManager.getSubtask(subtask1.getId());
        subtaskFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtaskFromManager);

        System.out.println("Update status task1: " + taskFromManager);
        System.out.println("Update status epic1: " + epicFromManager);
        System.out.println("Update status subtask1: " + subtaskFromManager);

        taskManager.deleteTask(taskFromManager.getId());
        System.out.println("Delete task: " + task1);

        taskManager.deleteEpic(epicFromManager.getId());
        System.out.println("Delete epic: " + epic1);

        System.out.println("-------- Оставшиеся объекты ----------------------------------------------------------------");

        for (Task  oneTask : taskManager.getAllTasks()) {
            System.out.println(oneTask);
        }
        for (Epic oneEpic : taskManager.getAllEpics()) {
            System.out.println(oneEpic);
        }

        for (Subtask oneSubtask : taskManager.getAllSubtasks()) {
            System.out.println(oneSubtask);
        }
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        //InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

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
