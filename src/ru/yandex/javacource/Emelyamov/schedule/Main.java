package ru.yandex.javacource.Emelyamov.schedule;

import ru.yandex.javacource.Emelyamov.schedule.model.Task;
import ru.yandex.javacource.Emelyamov.schedule.model.Epic;
import ru.yandex.javacource.Emelyamov.schedule.model.Subtask;
import ru.yandex.javacource.Emelyamov.schedule.model.TaskStatus;
import ru.yandex.javacource.Emelyamov.schedule.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("-------- Созданные объекты -----------------------------------------------------------------");
        Task task1 = taskManager.createTask(new Task("Задача 1", TaskStatus.NEW, "Описание задачи 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", TaskStatus.NEW, "Описание задачи 2"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", TaskStatus.NEW, "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", TaskStatus.DONE, "Описание подзадачи 1", epic1.getTaskId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", TaskStatus.NEW, "Описание подзадачи 2", epic1.getTaskId()));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", TaskStatus.NEW, "Описание эпика 2"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 4", TaskStatus.NEW, "Описание подзадачи 4", epic2.getTaskId()));

        for (Task  oneTask : taskManager.getAllTasks()) {
            System.out.println(oneTask);
        }
        for (Epic oneEpic : taskManager.getAllEpics()) {
            System.out.println(oneEpic);
        }

        for (Subtask oneSubtask : taskManager.getAllSubtasks()) {
            System.out.println(oneSubtask);
        }

        System.out.println("--------------------------------------------------------------------------------------------");

        Task taskFromManager = taskManager.getTask(task1.getTaskId());
        taskFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskFromManager);

        Epic epicFromManager = taskManager.getEpic(epic1.getTaskId());
        epicFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epicFromManager);

        Subtask subtaskFromManager = taskManager.getSubtask(subtask1.getTaskId());
        subtaskFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtaskFromManager);

        System.out.println("Update status task1: " + taskFromManager);
        System.out.println("Update status epic1: " + epicFromManager);
        System.out.println("Update status subtask1: " + subtaskFromManager);

        taskManager.deleteTask(taskFromManager.getTaskId());
        System.out.println("Delete task: " + task1);

        taskManager.deleteEpic(epicFromManager.getTaskId());
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
}
