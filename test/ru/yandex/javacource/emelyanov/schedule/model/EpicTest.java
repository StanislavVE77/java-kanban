package ru.yandex.javacource.emelyanov.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    @DisplayName("Должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Epic epic = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        Epic epicExpected = new Epic("Название эпика", TaskStatus.NEW, "Описание эпика");
        assertEqualsTask(epicExpected, epic, "Эпики должны совпадать по ");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
    }
}