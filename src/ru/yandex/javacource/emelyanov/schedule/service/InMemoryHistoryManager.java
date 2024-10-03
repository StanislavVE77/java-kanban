package ru.yandex.javacource.emelyanov.schedule.service;

import ru.yandex.javacource.emelyanov.schedule.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task item, Node next) {
            this.next = next;
            this.prev = prev;
            this.task = item;
        }
    }

    private Map<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    public InMemoryHistoryManager() {
        this.history = history;
    }

    @Override
    public List<Task> getAll() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
        history.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Node node = first; node != null; node = node.next) {
            tasks.add(node.task);
        }
        return tasks;
    }

    void linkLast(Task task) {
        final Node node = last;
        final Node newNode = new Node(node, task, null);
        last = newNode;
        if (node == null)
            first = newNode;
        else
            node.next = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }
        history.remove(node.task.getId());
    }
}
